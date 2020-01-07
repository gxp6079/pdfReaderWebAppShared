package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Template;
import main.webapp.Model.TemplateReader;
import main.webapp.Model.Token;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import spark.Request;
import spark.Response;
import spark.Route;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static spark.Spark.halt;


/**
 * first route called
 *
 * provide pdf file and name of template
 * @redirect getTemplateRoute if the template name is found in the database
 * @redirect getTableInfoRoute if the template name is not found
 */
public class postTemplateRoute implements Route {
    private static final Logger LOG = Logger.getLogger(postTemplateRoute.class.getName());
    public static FileHandler fh;
    private static final String API_KEY = "bbdro1wrndmx";
    private static final String FORMAT = "csv";

    public postTemplateRoute() {
        try{
            fh = new FileHandler("pdfReaderLogFiles/PostTamplateRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        }
        catch (Exception e){

        }
        LOG.info("postTemplateRoute initialized");
    }


    private String downloadFile(Request request) {
        try {
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement("/tmp")); // MultipartConfigElement("/", 1000000000, 10000000, 1024));


            final File upload = new File("upload");
            if (!upload.exists() && !upload.mkdirs()) {
                throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
            }

            // apache commons-fileupload to handle file upload
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(upload);
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            List<FileItem> items = fileUpload.parseRequest(request.raw());

            // image is the field name that we want to save
            FileItem item = items.stream()
                    .filter(e -> "uploaded_file".equals(e.getFieldName()))
                    .findFirst().get();
            String fileName = item.getName();
            item.write(new File(upload.getAbsolutePath(), fileName));

            LOG.info("File created");

            Path p = Paths.get(upload.getAbsolutePath()).toAbsolutePath();

            LOG.info("returned the path" + p.toString() +"/" +fileName);

            return p.toString() + "/" +fileName;

        } catch (Exception e) {
            Path p = Paths.get(Paths.get("").toAbsolutePath().toString() + "/temp/" + "NewPDF.csv").toAbsolutePath();
            LOG.info(e.getMessage());
            return p.toString() + "/NewPDF.csv";
        }
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {

        String tokenId = request.queryParams("token");
        LOG.info("Got token");
        Token token = Application.getToken(tokenId, request);
        LOG.info("found token: " + token);

        String templateType = request.queryParams("type");
        LOG.info("got template type");
        token.setTemplate(new Template(templateType));
        LOG.info("added template");

        String path = downloadFile(request);
        LOG.info("downloaded");
        if (path == null) {
            response.status(400);
            return "Error loading file from request body";
        }

        convertToCSV(path);

        LOG.info("Converted");

        String encoding = "UTF-8";
        response.raw().setContentType("text/html; charset=" + encoding);
        response.raw().setCharacterEncoding(encoding);


        // Template fromDB = TemplateReader.readFromDB(templateType);

        String csvFilePath = getOutputFilename(path, "csv");

        LOG.info("setting new path");
        token.setCsvPath(csvFilePath);
        LOG.info("new path set");
        LOG.info("setting new pdf path");
        token.setPdfPath(path);
        LOG.info("new PDF path set");

        if (TemplateReader.checkIfExists(templateType)) {
            LOG.info("template exists");
            try {
                TemplateReader.readExistingTemplate(csvFilePath, templateType, response.raw().getWriter());
            }
            catch (Exception e){
                LOG.info("Error reading existing template:" +  e.getMessage());
            }
            return 1;
        }


        Template currentTemplate = token.getTemplate();
        currentTemplate.setType(templateType);

        List<String[]> lines = TemplateReader.readAllLines(csvFilePath);

        token.setTableFactory(new TableFactory(lines));

        return 1;
    }


    private static String loadEncoding(String csvPath) throws IOException {
        File in =  new File(csvPath);
        InputStreamReader r = new InputStreamReader(new FileInputStream(in));
        String encoding = r.getEncoding();
        r.close();
        return encoding;
    }


    public static boolean convertToCSV(String filename) {


        final String apiKey = API_KEY;
        final String format = FORMAT;
        final String pdfFilename = filename;


        // Avoid cookie warning with default cookie configuration
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

        File inputFile = new File(pdfFilename);

        LOG.info("got file to make csv");

        if (!inputFile.canRead()) {
            System.out.println("Can't read input PDF file: \"" + pdfFilename + "\"");
            System.exit(1);
        }

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build()) {
            HttpPost httppost = new HttpPost("https://pdftables.com/api?format=" + format + "&key=" + apiKey);
            FileBody fileBody = new FileBody(inputFile);

            HttpEntity requestBody = MultipartEntityBuilder.create().addPart("f", fileBody).build();
            httppost.setEntity(requestBody);

            LOG.info("Sending request");

            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    LOG.info(response.getStatusLine().toString());
                    System.exit(1);
                }
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    final String outputFilename = getOutputFilename(pdfFilename, format.replaceFirst("-.*$", ""));
                    LOG.info("Writing output to " + outputFilename);

                    final File outputFile = new File(outputFilename);
                    FileUtils.copyToFile(resEntity.getContent(), outputFile);
                    return true;
                } else {
                    LOG.info("Error: file missing from response");
                    return false;
                    // System.exit(1);
                }
            }
        } catch (Exception e) {
            LOG.info(e.toString());
            return false;
        }
    }

    private static String getOutputFilename(String pdfFilename, String suffix) {
        if (pdfFilename.length() >= 5 && pdfFilename.toLowerCase().endsWith(".pdf")) {
            return pdfFilename.substring(0, pdfFilename.length() - 4) + "." + suffix;
        } else {
            return pdfFilename + "." + suffix;
        }
    }

}
