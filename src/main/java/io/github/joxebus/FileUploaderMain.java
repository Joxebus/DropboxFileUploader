package io.github.joxebus;

import java.io.File;
import java.net.URL;
import java.util.List;

import io.github.joxebus.service.FileUploaderService;

public class FileUploaderMain {

    public static void main(String[] args) {
        ClassLoader classLoader = FileUploaderMain.class.getClassLoader();
        URL resource = classLoader.getResource(".");
        FileUploaderService service = new FileUploaderService();
        File dir = new File(resource.getFile());


        for(File file : dir.listFiles()) {
            if(file.isFile()) {
                service.upload(file);
            }
        }

        List<String> filenames = service.listFileNames();

        for(String filename : filenames) {
            service.download(filename);
        }
    }
}
