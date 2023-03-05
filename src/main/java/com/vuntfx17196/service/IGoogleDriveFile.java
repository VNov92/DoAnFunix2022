package com.vuntfx17196.service;

import com.vuntfx17196.dto.GoogleDriveFileDTO;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IGoogleDriveFile {

  List<GoogleDriveFileDTO> getAllFile() throws IOException, GeneralSecurityException;

  void deleteFile(String id) throws Exception;

  /**
   * @param file     tệp được chọn để upload lên GG Drive
   * @param filePath thư mục gốc
   * @param isPublic trạng thái share hoặc private
   * @return id của tệp
   */
  String uploadFile(MultipartFile file, String filePath, boolean isPublic);

  void downloadFile(String id, OutputStream outputStream)
      throws IOException, GeneralSecurityException;
}
