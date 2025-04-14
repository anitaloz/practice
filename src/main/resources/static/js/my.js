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

document.addEventListener('DOMContentLoaded', function() {
    const readMoreLinks = document.querySelectorAll('.read-more-link');

    readMoreLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault(); // Предотвратить переход по ссылке

            const descriptionContainer = this.parentNode;
            const shortDescription = descriptionContainer.querySelector('.description-short');
            const fullDescription = descriptionContainer.querySelector('.description-full');

            shortDescription.classList.toggle('description-hidden');
            fullDescription.classList.toggle('description-hidden');

            if (fullDescription.classList.contains('description-hidden')) {
                this.textContent = 'Подробнее';
            } else {
                this.textContent = 'Свернуть';
            }
        });
    });
});

