package main.webapp.Model;
//
//import com.google.gson.Gson;
//import org.apache.commons.io.FileUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.config.CookieSpecs;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import spark.Request;
//import spark.Response;
//import spark.Route;
//
//import javax.servlet.ServletOutputStream;
//import java.io.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Scanner;
//
//import static spark.Spark.get;
//
//public class PDFToCSV {
//
//    private static final String API_KEY = "el9x8gb285ar";
//    private static final String FORMAT = "csv";
//
//    public static void main(String[] args) {
//
//        Gson gson = new Gson();
//        get("/PDFreader", new Route() {
//            public Object handle(Request request, Response response) throws IOException {
//                run_with_pdf(response.raw().getOutputStream());
//                return 1;
//            }
//
//        });
//        get("/postFile", new Route() {
//            @Override
//            public Object handle(Request request, Response response) throws Exception {
//                request.body();
//                return null;
//            }
//        });
//        String databaseUrl = "jdbc:mysql://129.21.117.231/PDFreader?serverTimezone=EST";

//        try {
//            Connection connectionSource = DriverManager.getConnection(databaseUrl, "brit", "x0EspnYA8JaqCPT9");
//            Statement s = connectionSource.createStatement();
//            //int Result=s.executeUpdate("CREATE DATABASE PDFreader");
//            String table = "CREATE TABLE IF NOT EXISTS `TEMPLATES` (\n" +
//                    "`template_type` varchar(50) NOT NULL,\n" +
//                    "`template_object` blob,\n" +
//                    "PRIMARY KEY (`template_type`)\n" +
//                    ")";
//            int Result=s.executeUpdate(table);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void run_with_pdf(ServletOutputStream out) throws IOException {
//        Scanner scan = new Scanner(System.in);
//        TemplateReader reader = new TemplateReader();
//        out.println("Enter pdf file name:");
//        String filename = scan.nextLine().trim();
//        out.println("Enter the template type:");
//        String templateName = scan.nextLine().trim();
//
//        while (!convertToCSV(filename)) {
//            out.println("Convert failed, enter new filename: ");
//            filename = scan.nextLine().trim();
//        }
//
//        //reader.readTemplate(getOutputFilename(filename, "csv"), templateName, out);
//    }


//    public static boolean convertToCSV(String filename) {
//
//
//        final String apiKey = API_KEY;
//        final String format = FORMAT;
//        final String pdfFilename = filename;
//
//
//        // Avoid cookie warning with default cookie configuration
//        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
//
//        File inputFile = new File(pdfFilename);
//
//        if (!inputFile.canRead()) {
//            System.out.println("Can't read input PDF file: \"" + pdfFilename + "\"");
//            System.exit(1);
//        }
//
//        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build()) {
//            HttpPost httppost = new HttpPost("https://pdftables.com/api?format=" + format + "&key=" + apiKey);
//            FileBody fileBody = new FileBody(inputFile);
//
//            HttpEntity requestBody = MultipartEntityBuilder.create().addPart("f", fileBody).build();
//            httppost.setEntity(requestBody);
//
//            System.out.println("Sending request");
//
//            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
//                if (response.getStatusLine().getStatusCode() != 200) {
//                    System.out.println(response.getStatusLine());
//                    System.exit(1);
//                }
//                HttpEntity resEntity = response.getEntity();
//                if (resEntity != null) {
//                    final String outputFilename = getOutputFilename(pdfFilename, format.replaceFirst("-.*$", ""));
//                    System.out.println("Writing output to " + outputFilename);
//
//                    final File outputFile = new File(outputFilename);
//                    FileUtils.copyToFile(resEntity.getContent(), outputFile);
//                    return true;
//                } else {
//                    System.out.println("Error: file missing from response");
//                    return false;
//                    // System.exit(1);
//                }
//            }
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private static String getOutputFilename(String pdfFilename, String suffix) {
//        if (pdfFilename.length() >= 5 && pdfFilename.toLowerCase().endsWith(".pdf")) {
//            return pdfFilename.substring(0, pdfFilename.length() - 4) + "." + suffix;
//        } else {
//            return pdfFilename + "." + suffix;
//        }
//    }
//
//
//}
