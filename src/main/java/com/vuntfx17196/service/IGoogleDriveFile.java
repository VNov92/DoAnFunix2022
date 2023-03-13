package com.vuntfx17196.service;

import com.google.api.services.drive.model.File;
import com.vuntfx17196.dto.GoogleDriveFileDTO;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IGoogleDriveFile {

  GoogleDriveFileDTO getFile(String id) throws IOException, GeneralSecurityException;

  File update(String fileId, MultipartFile file);

  List<GoogleDriveFileDTO> getAllFile() throws IOException, GeneralSecurityException;

  void deleteFile(String id) throws IOException, GeneralSecurityException;

  /**
   * @param file     tệp được chọn để upload lên GG Drive
   * @param filePath thư mục gốc
   * @param isPublic trạng thái share hoặc private
   * @return id của tệp
   */
  File uploadFile(MultipartFile file, String filePath, boolean isPublic);

  void downloadFile(String id, OutputStream outputStream)
      throws IOException, GeneralSecurityException;
}
