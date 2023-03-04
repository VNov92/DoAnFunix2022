package com.vuntfx17196.service.impl;

import com.google.api.services.drive.model.File;
import com.vuntfx17196.dto.GoogleDriveFoldersDTO;
import com.vuntfx17196.service.IGoogleDriveFolder;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IGoogleDriveFolderImpl implements IGoogleDriveFolder {

  private final GoogleFileManager googleFileManager;

  public IGoogleDriveFolderImpl(GoogleFileManager googleFileManager) {
    this.googleFileManager = googleFileManager;
  }

  @Override
  public List<GoogleDriveFoldersDTO> getAllFolder() throws IOException, GeneralSecurityException {
    List<File> files = googleFileManager.listFolderContent("root");
    List<GoogleDriveFoldersDTO> responseList = null;
    GoogleDriveFoldersDTO dto = null;

    if (files != null) {
      responseList = new ArrayList<>();
      for (File f : files) {
        dto = new GoogleDriveFoldersDTO();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setLink("https://drive.google.com/drive/u/3/folders/" + f.getId());
        responseList.add(dto);
      }
    }
    return responseList;
  }

  @Override
  public void createFolder(String folderName) throws Exception {
    String folderId = googleFileManager.getFolderId(folderName);
    System.out.println(folderId);
  }

  @Override
  public void deleteFolder(String id) throws Exception {
    googleFileManager.deleteFileOrFolder(id);
  }
}
