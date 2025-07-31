import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultithreadedFileDownloader {


    public static byte[] downloadChunk(String url, long start, long end) throws Exception {

        System.out.println("Downloading from " + (start / (1024 * 1024)) + "mb to " + (end / (1024 * 1024)) + " mb");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Range", "bytes=" + start + "-" + end)
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 206) {
            throw new RuntimeException("Server didn't return partial content. Status: " + response.statusCode());
        }
        System.out.println("Download complete from  " + (start / (1024 * 1024)) + "mb to " + (end / (1024 * 1024)) + " mb");

        return response.body();
    }


    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();

        if (args.length < 1) {
            throw new IllegalArgumentException("Please specify url");
        }

        String uri = args[0];

        System.out.println("----Starting Multithreaded File Downloader----");

        HttpClient httpClient = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .HEAD()
                .header("Content-Type", "application/json")
                .uri(new URI(uri))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


        Map<String, List<String>> simplifiedHeaders = httpResponse.headers().map();

        long contentLength = Long.parseLong(simplifiedHeaders.get("Content-Length").get(0));

        // to get the file name if the header is present
        String contentDisposition = httpResponse.headers().firstValue("Content-Disposition").orElse(null);

        int numThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        long chunkSize =  contentLength / numThreads;

        String fileName = null;

        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            int index = contentDisposition.indexOf("filename=");
            fileName = contentDisposition.substring(index + 9).replaceAll("\"", "");
        }

        if (fileName == null) {
            fileName = uri.substring(uri.lastIndexOf('/') + 1);
        }

        for(int i = 0; i < numThreads; i++) {

            long start = i * chunkSize;
            long end = (i == numThreads - 1) ? contentLength - 1 : start + chunkSize - 1;

            String finalFileName = fileName;

            executorService.submit(() -> {

                byte[] data;
                try {
                    data = downloadChunk(uri, start, end);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                try(RandomAccessFile file = new RandomAccessFile(finalFileName, "rw")){
                    file.seek(start);
                    file.write(data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }

        executorService.shutdown();

        if(executorService.awaitTermination(2, TimeUnit.HOURS)) {
            System.out.println("Download complete took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        }

    }
}