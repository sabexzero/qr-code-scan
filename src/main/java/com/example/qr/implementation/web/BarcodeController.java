package com.example.qr.implementation.web;

import com.example.qr.implementation.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BarcodeController {
    private final BarcodeService barcodeService;

    /**
     * Главная страница с формой загрузки.
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Принимает загруженный файл, передает его в сервис для обработки
     * и возвращает результат на страницу.
     * @param imageFile Загруженный пользователем файл.
     * @param model Модель для передачи данных в Thymeleaf.
     * @return Имя шаблона Thymeleaf.
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (imageFile.isEmpty()) {
            model.addAttribute("error", "Пожалуйста, выберите файл для загрузки.");
            return "index";
        }

        try {
            String decodedText = barcodeService.decodeBarcode(imageFile);

            if (decodedText == null || decodedText.isBlank()) {
                model.addAttribute("error", "Штрих-код на изображении не найден или не может быть прочитан.");
            } else {
                model.addAttribute("decodedText", decodedText);
            }

        } catch (Exception e) {
            log.error("Ошибка при распознавании штрих-кода в файле: {}", imageFile.getOriginalFilename(), e);
            model.addAttribute("error", "Произошла внутренняя ошибка при обработке изображения. Пожалуйста, попробуйте другой файл.");
        }

        return "index";
    }
}