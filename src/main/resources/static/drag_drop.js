const fileBox = document.querySelector('.file_box');
const fileNameElement = document.getElementById('file-name');
const fileInput = document.getElementById('file-input');
const selectFileButton = document.getElementById('file');

fileBox.addEventListener('dragenter', (e) => {
    e.preventDefault();
    e.stopPropagation();
    fileBox.classList.add('highlight');
});

fileBox.addEventListener('dragleave', (e) => {
    e.preventDefault();
    e.stopPropagation();
    fileBox.classList.remove('highlight');
});

fileBox.addEventListener('dragover', (e) => {
    e.preventDefault();
    e.stopPropagation();
});

fileBox.addEventListener('drop', (e) => {
    e.preventDefault();
    e.stopPropagation();
    fileBox.classList.remove('highlight');

    const files = e.dataTransfer.files;
    handleFiles(files);
});

// 파일 선택 버튼 클릭 이벤트 처리
selectFileButton.addEventListener('click', () => {
    fileInput.click();
});

// 파일 선택 이벤트 처리
fileInput.addEventListener('change', () => {
    const selectedFiles = fileInput.files;
    handleFiles(selectedFiles);
});


// 파일 드롭 이벤트 처리
function handleFiles(files) {
    if (files.length > 0) {
        const file = files[0];
        fileNameElement.textContent = file.name;
        fileInput.files = files;

    }

}