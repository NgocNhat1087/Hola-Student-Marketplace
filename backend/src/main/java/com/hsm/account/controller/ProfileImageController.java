package com.hsm.account.controller;

import com.hsm.account.entity.User;
import com.hsm.account.service.AccountService;
import com.hsm.common.ApiException;
import com.hsm.security.SessionFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileImageController {
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private final AccountService service;
    private final Path root = Path.of(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();

    public ProfileImageController(AccountService service) { this.service = service; }

    @PostMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object image(@RequestParam String kind, @RequestPart MultipartFile file,
                        HttpServletRequest request) throws IOException {
        User user = (User) request.getAttribute(SessionFilter.USER);
        if (user == null) throw new ApiException(401, "UNAUTHENTICATED", "Authentication is required.");
        if (!kind.equals("avatar") && !kind.equals("cover"))
            throw new ApiException(400, "INVALID_IMAGE_KIND", "Image kind must be avatar or cover.");

        String format = detectImageFormat(file);
        if (file.isEmpty() || file.getSize() > MAX_IMAGE_SIZE || format == null)
            throw new ApiException(400, "INVALID_IMAGE", "Only valid JPG or PNG images up to 5 MB are accepted.");

        Files.createDirectories(root);
        String extension = format.equals("png") ? ".png" : ".jpg";
        String name = user.getId() + "-" + kind + "-" + UUID.randomUUID() + extension;
        Path target = root.resolve(name).normalize();
        if (!target.getParent().equals(root))
            throw new ApiException(400, "INVALID_IMAGE", "Invalid upload path.");
        file.transferTo(target);
        try {
            return service.updateImage(user.getId(), kind, "/uploads/" + name);
        } catch (RuntimeException exception) {
            Files.deleteIfExists(target);
            throw exception;
        }
    }

    private String detectImageFormat(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;
        try (var input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) return null;
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) return null;
            String format = readers.next().getFormatName().toLowerCase(Locale.ROOT);
            return format.equals("png") ? "png" : (format.equals("jpeg") || format.equals("jpg") ? "jpg" : null);
        }
    }
}
