package com.vuntfx17196.service.impl;

import com.google.api.services.drive.model.File;
import com.vuntfx17196.dto.GoogleDriveFileDTO;
import com.vuntfx17196.service.IGoogleDriveFile;
import com.vuntfx17196.utils.ConvertByteToBigger;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IGoogleDriveFileImpl implements IGoogleDriveFile {

  private final GoogleFileManager googleFileManager;

  public IGoogleDriveFileImpl(GoogleFileManager googleFileManager) {
    this.googleFileManager = googleFileManager;
  }

  @Override
  public List<GoogleDriveFileDTO> getAllFile() throws IOException, GeneralSecurityException {
    List<GoogleDriveFileDTO> responseList = null;
    List<File> files = googleFileManager.listFiles();
    GoogleDriveFileDTO dto = null;

    if (files != null) {
      responseList = new ArrayList<>();
      for (File f : files) {
        dto = new GoogleDriveFileDTO();
        if (f.getSize() != null) {
          dto.setId(f.getId());
          dto.setName(f.getName());
          dto.setThumbnailLink(f.getThumbnailLink());
          dto.setSize(ConvertByteToBigger.getSize(f.getSize()));
          dto.setLink("https://drive.google.com/file/d/" + f.getId() + "/view?usp=sharing");
          dto.setShared(f.getShared());

          responseList.add(dto);
        }
      }
    }
    return responseList;
  }

  @Override
  public void deleteFile(String id) throws Exception {
    googleFileManager.deleteFileOrFolder(id);
  }
  
  @Override
  public String uploadFile(MultipartFile file, String filePath, boolean isPublic) {
    String type = "";
    String role = "";
    if (isPublic) {
      // Public file of folder for everyone
      type = "anyone";
      role = "reader";
    } else {
      // Private
      type = "private";
      role = "private";
    }
    return googleFileManager.uploadFile(file, filePath, type, role);
  }

  @Override
  public void downloadFile(String id, OutputStream outputStream)
      throws IOException, GeneralSecurityException {
    googleFileManager.downloadFile(id, outputStream);
  }
}
