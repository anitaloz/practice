document.addEventListener("DOMContentLoaded", function() {
    const myBookAddButton = document.getElementById("MyBookAdd");
    const formContainer1 = document.getElementById('formSearchingContainer1');

    myBookAddButton.addEventListener('click', () => {
        fetch('/add')  // URL endpoint, который возвращает addBookForm.html
            .then(response => response.text())
            .then(html => {
                formContainer1.innerHTML = html;
            })
            .catch(error => {
                console.error('Error fetching form:', error);
                formContainer1.innerHTML = '<p>Ошибка загрузки формы.</p>';
            });
    });
});
