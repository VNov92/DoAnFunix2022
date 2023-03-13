package com.vuntfx17196.service.impl;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.vuntfx17196.configuration.GoogleDriveConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GoogleFileManager {

  private final GoogleDriveConfig googleDriveConfig;

  public GoogleFileManager(GoogleDriveConfig googleDriveConfig) {
    this.googleDriveConfig = googleDriveConfig;
  }

  // update file to new version
  public File update(MultipartFile file, String fileId) {
    File fileMetadata = new File();
    fileMetadata.setName(file.getOriginalFilename());
    try {
      return googleDriveConfig.getInstance().files()
          .update(fileId, fileMetadata, new InputStreamContent(
              file.getContentType(), new ByteArrayInputStream(file.getBytes())
          )).setFields("thumbnailLink").execute();
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }
    return null;
  }

  // tim kiem 1 file cu the
  public File search(String id) throws IOException, GeneralSecurityException {
    return googleDriveConfig.getInstance().files().get(id)
        .setFields("id, name, size, thumbnailLink, shared").execute();
  }

  // hien thi tat ca cac file
  public List<File> listFiles() throws IOException, GeneralSecurityException {
    // Print the names and IDs for up to 10 files.
    FileList result = googleDriveConfig.getInstance().files().list()
        .setPageSize(1000)
        .setFields(
            "nextPageToken, files(id, name, size, thumbnailLink, shared)") // get field of google drive file
        .execute();
    return result.getFiles();
  }

  // hien thi tat ca cac thu muc
  public List<File> listFolderContent(String parentId)
      throws IOException, GeneralSecurityException {
    if (parentId == null) {
      parentId = "root";
    }
    String query = "'" + parentId + "' in parents";
    FileList result = googleDriveConfig.getInstance().files().list()
        .setQ(query)
        .setPageSize(10)
        .setFields("nextPageToken, files(id, name)") // get field of google drive folder
        .execute();
    return result.getFiles();
  }

  // Tai tep len thu muc
  public File uploadFile(MultipartFile file, String folderName, String type, String role) {
    try {
      String folderId = getFolderId(folderName);
      if (null != file) {

        File fileMetadata = new File();
        fileMetadata.setParents(Collections.singletonList(folderId));
        fileMetadata.setName(file.getOriginalFilename());
        File uploadFile = googleDriveConfig.getInstance()
            .files()
            .create(fileMetadata, new InputStreamContent(
                file.getContentType(),
                new ByteArrayInputStream(file.getBytes()))
            )
            .setFields("id, thumbnailLink").execute();

        if (!type.equals("private") && !role.equals("private")) {
          // Call Set Permission drive
          googleDriveConfig.getInstance().permissions()
              .create(uploadFile.getId(), setPermission(type, role)).execute();
        }

        return uploadFile;
      }
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }
    return null;
  }

  // Tai tep xuong voi id
  public void downloadFile(String id, OutputStream outputStream)
      throws IOException, GeneralSecurityException {
    if (id != null) {
      googleDriveConfig.getInstance().files()
          .get(id).executeMediaAndDownloadTo(outputStream);
    }
  }

  // Xoa tep voi id
  public void deleteFileOrFolder(String fileId) throws IOException, GeneralSecurityException {
    googleDriveConfig.getInstance().files().delete(fileId).execute();
  }

  // Cai dat quyen cho tep
  private Permission setPermission(String type, String role) {
    Permission permission = new Permission();
    permission.setType(type);
    permission.setRole(role);
    return permission;
  }

  // Lay id thu muc
  public String getFolderId(String folderName) throws IOException, GeneralSecurityException {
    String parentId = null;
    String[] folderNames = folderName.split("/");

    Drive driveInstance = googleDriveConfig.getInstance();
    for (String name : folderNames) {
      parentId = findOrCreateFolder(parentId, name, driveInstance);
    }
    return parentId;
  }

  private String findOrCreateFolder(String parentId, String folderName, Drive driveInstance)
      throws IOException {
    String folderId = searchFolderId(parentId, folderName, driveInstance);
    // Folder already exists, so return id
    if (folderId != null) {
      return folderId;
    }
    //Folder dont exists, create it and return folderId
    File fileMetadata = new File();
    fileMetadata.setMimeType("application/vnd.google-apps.folder");
    fileMetadata.setName(folderName);

    if (parentId != null) {
      fileMetadata.setParents(Collections.singletonList(parentId));
    }
    return driveInstance.files().create(fileMetadata)
        .setFields("id")
        .execute()
        .getId();
  }

  private String searchFolderId(String parentId, String folderName, Drive service)
      throws IOException {
    String folderId = null;
    String pageToken = null;
    FileList result = null;

    File fileMetadata = new File();
    fileMetadata.setMimeType("application/vnd.google-apps.folder");
    fileMetadata.setName(folderName);

    do {
      String query = " mimeType = 'application/vnd.google-apps.folder' ";
      if (parentId == null) {
        query = query + " and 'root' in parents";
      } else {
        query = query + " and '" + parentId + "' in parents";
      }
      result = service.files().list().setQ(query)
          .setSpaces("drive")
          .setFields("nextPageToken, files(id, name)")
          .setPageToken(pageToken)
          .execute();

      for (File file : result.getFiles()) {
        if (file.getName().equalsIgnoreCase(folderName)) {
          folderId = file.getId();
        }
      }
      pageToken = result.getNextPageToken();
    } while (pageToken != null && folderId == null);

    return folderId;
  }
}
