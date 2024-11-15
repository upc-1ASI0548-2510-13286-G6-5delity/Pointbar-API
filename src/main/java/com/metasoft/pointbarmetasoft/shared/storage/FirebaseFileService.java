package com.metasoft.pointbarmetasoft.shared.storage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseFileService {
    @Value("${firebase.credentials}")
    private String firebaseCredentials;
    private Storage storage;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {
            InputStream serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes(StandardCharsets.UTF_8));
            storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("pointbar-application")
                    .build()
                    .getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            String imageName = generateFileName(file.getOriginalFilename());
            Map<String, String> map = new HashMap<>();
            map.put("firebaseStorageDownloadTokens", imageName);
            String objectName = "images/products/" + imageName;
            BlobId blobId = BlobId.of("pointbar-application.appspot.com", objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setMetadata(map)
                    .setContentType(file.getContentType())
                    .build();
            storage.create(blobInfo, file.getInputStream().readAllBytes( ));

            // Construir y devolver la URL de la imagen subida
            String DOWNLOAD_URL= "https://firebasestorage.googleapis.com/v0/b/pointbar-application.appspot.com/o/images%2Fproducts%2F";
            return DOWNLOAD_URL + imageName + "?alt=media&token=" + imageName;
        } catch (Exception e) {
            throw new IOException("Failed to upload image to Firebase", e);
        }

    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "." + getExtension(originalFileName);
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }
}
