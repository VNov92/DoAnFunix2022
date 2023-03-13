package com.vuntfx17196.service;

import com.vuntfx17196.dto.GoogleDriveFoldersDTO;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface IGoogleDriveFolder {

  List<GoogleDriveFoldersDTO> getAllFolder() throws IOException, GeneralSecurityException;

  void createFolder(String folderName) throws IOException, GeneralSecurityException;

  void deleteFolder(String id) throws IOException, GeneralSecurityException;
}
