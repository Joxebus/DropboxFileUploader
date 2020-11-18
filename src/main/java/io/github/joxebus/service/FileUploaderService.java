package io.github.joxebus.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class FileUploaderService {

    // Better have this configuration as an ENV variable and read from there
    private static final String DROPBOX_TOKEN_ACCESS = "<replace-with-your-dropbox-key-generated>";
    // If all your files goes to the root app directory you need to indicate as this
    private static final String DROPBOX_PATH_FOLDER = "/";
    // Change for your local folder
    private static final String DOWNLOAD_FOLDER = "/the/path/to/your/downloads";
    private final DbxClientV2 client;

    public FileUploaderService() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("java-sample").build();
        client = new DbxClientV2(config, DROPBOX_TOKEN_ACCESS);
    }

    public String upload(File file) {
        try {
            System.out.printf("Uploading file [%s] to dropbox \n",file.getName());
            // Note: You need to indicate a folder path with / at the beginning
            FileMetadata metadata = client.files().uploadBuilder(DROPBOX_PATH_FOLDER.concat(file.getName()))
                    .uploadAndFinish(new FileInputStream(file));
            return metadata.getName();
        } catch ( DbxException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public List<String> listFileNames() {
        try {
            // Note: Empty to return all the files on the root app folder
            ListFolderResult listFolderResult = client.files().listFolder("");
            return listFolderResult.getEntries()
                    .stream()
                    .map(Metadata::getName)
                    .collect(Collectors.toList());
        } catch (DbxException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public void download(String filename) {
        try {
            // Note: You need to indicate a folder path with / at the beginning
            DbxDownloader download = client.files().download(DROPBOX_PATH_FOLDER.concat(filename));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            download.download(out);

            System.out.printf("Saving copy on [%s] for image [%s] \n", DOWNLOAD_FOLDER, filename);
            // Note: Verify you have this folder on your system or
            // change the code to create the folder if not exists
            FileOutputStream fos = new FileOutputStream(new File(DOWNLOAD_FOLDER, filename));
            fos.write(out.toByteArray());
            fos.close();
            out.close();
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }
}
