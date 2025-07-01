//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//@Service
//public class FileStorageService {
//
//    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
//
//    public FileStorageService() {
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (IOException ex) {
//            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
//
//    public String storeFile(String type, MultipartFile file) {
//        try {
//            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//            Path targetLocation = this.fileStorageLocation.resolve(Paths.get(type).resolve(fileName));
//            Files.createDirectories(targetLocation.getParent()); // Đảm bảo folder con tồn tại
//
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//            return fileName;
//        } catch (IOException ex) {
//            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
//        }
//    }
//
//    public Resource loadFile(String type, String fileName) throws IOException {
//        try {
//            Path filePath = this.fileStorageLocation.resolve(Paths.get(type).resolve(fileName)).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//            if (resource.exists()) {
//                return resource;
//            } else {
//                throw new IOException("File not found " + fileName);
//            }
//        } catch (MalformedURLException ex) {
//            throw new IOException("File not found " + fileName, ex);
//        }
//    }
//
//    public String getContentType(Resource resource) throws IOException {
//        Path path = resource.getFile().toPath();
//        return Files.probeContentType(path);
//    }
//}
