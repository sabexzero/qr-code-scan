document.addEventListener('DOMContentLoaded', () => {
    const uploadForm = document.getElementById('upload-form');
    // Если формы нет (например, на странице уже есть результат), ничего не делаем
    if (!uploadForm) {
        return;
    }

    const imageInput = document.getElementById('imageFile');
    const uploader = document.querySelector('.uploader');

    // Контейнеры состояний
    const uploadState = document.getElementById('upload-state');
    const previewState = document.getElementById('preview-state');
    const loadingState = document.getElementById('loading-state');

    // Элементы предпросмотра
    const previewImage = document.getElementById('preview-image');
    const filenameDisplay = document.getElementById('filename');
    const changeFileBtn = document.getElementById('change-file-btn');

    // Функция для переключения состояний
    const switchState = (state) => {
        uploadState.classList.add('hidden');
        previewState.classList.add('hidden');
        loadingState.classList.add('hidden');

        if (state === 'upload') uploadState.classList.remove('hidden');
        else if (state === 'preview') previewState.classList.remove('hidden');
        else if (state === 'loading') loadingState.classList.remove('hidden');
    };

    // Обработка файлов
    const handleFiles = (files) => {
        if (!files || files.length === 0) {
            switchState('upload');
            return;
        }

        const file = files[0];
        if (!file.type.startsWith('image/')) {
            alert('Пожалуйста, выберите файл изображения.');
            return;
        }

        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        imageInput.files = dataTransfer.files;

        const reader = new FileReader();
        reader.onload = (e) => {
            previewImage.src = e.target.result;
            filenameDisplay.textContent = file.name;
            switchState('preview');
        };
        reader.readAsDataURL(file);
    };

    // События
    uploader.addEventListener('click', () => imageInput.click());
    changeFileBtn.addEventListener('click', () => imageInput.click());
    imageInput.addEventListener('change', () => handleFiles(imageInput.files));

    // Drag & Drop
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        uploader.addEventListener(eventName, e => {
            e.preventDefault();
            e.stopPropagation();
        }, false);
    });
    ['dragenter', 'dragover'].forEach(eventName => {
        uploader.addEventListener(eventName, () => uploader.classList.add('dragover'), false);
    });
    ['dragleave', 'drop'].forEach(eventName => {
        uploader.addEventListener(eventName, () => uploader.classList.remove('dragover'), false);
    });
    uploader.addEventListener('drop', e => handleFiles(e.dataTransfer.files));

    // Вставка из буфера обмена
    document.addEventListener('paste', e => {
        if (e.clipboardData.files.length > 0) {
            handleFiles(e.clipboardData.files);
        }
    });

    // Отправка формы
    uploadForm.addEventListener('submit', () => {
        switchState('loading');
    });

});