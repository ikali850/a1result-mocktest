$(document).ready(function () {
    var currentIndex = document.getElementById("currentIndex").value;
    var totalQuestions = document.getElementById("totalQuestions").value;

    function showToast(message) {
        // Set the message
        document.getElementById('toastMessage').textContent = message;

        // Show the toast
        var toastElement = new bootstrap.Toast(document.getElementById('errorToast'));
        toastElement.show();
    }
    document.addEventListener('contextmenu', function (e) {
        e.preventDefault();
        showToast('Right-click is disabled!');
    });

    function updateProgress() {
        var progressPercentage = (currentIndex / totalQuestions) * 100;
        progressPercentage = Math.min(Math.max(progressPercentage, 0), 100);
        $('#progressBar')
            .css('width', progressPercentage + '%')
            .attr('aria-valuenow', progressPercentage)
            .text('Q' + currentIndex + '/' + totalQuestions);
    }

    function updateButtons() {
        if (currentIndex <= 0) {
            $("#previousBtn").prop("disabled", true);
        } else {
            $("#previousBtn").prop("disabled", false);
        }
    }

    function toggleLoader() {
        $('#loaderOverlay').toggle();
    }

    $('#questionForm').on('submit', function (e) {
        e.preventDefault();
        toggleLoader();
        var selectedOption = $('input[name="selectedOption"]:checked').val();
        if (!selectedOption) {
            showToast('Please select an option!');
            toggleLoader();
            return;
        }

        var data = {
            question: $('#questionTitle').text(),
            selectedOption: selectedOption,
            correctAnswer: $('#correctAnswerHidden').val()
        };

        $.ajax({
            url: '/mock/test/saveAnswer',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                if (response && response.id) {
                    updateForm(response);
                    currentIndex++;
                    updateProgress();
                    updateButtons();
                    if (currentIndex == totalQuestions - 1) {
                        $('#nextBtn').toggle();
                    }
                    toggleLoader();
                } else {
                    window.location.href = '/mock/test/submit';
                    toggleLoader();
                }
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
                showToast('Failed to save answer. Please try again!');
                toggleLoader();
            }
        });
    });

    $("#previousBtn").click(function () {
        toggleLoader();
        $.ajax({
            url: '/mock/test/prevQuestion',
            method: 'GET',
            success: function (response) {
                updateForm(response);
                currentIndex--;
                updateProgress();
                updateButtons();
                toggleLoader();
                if (currentIndex < totalQuestions - 1) {
                    $('#nextBtn').show();
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching previous question:", error);
                showToast('Failed to load previous question!');
                toggleLoader();
            }
        });
    });

    function updateForm(question) {
        $('#questionTitle').text('Q' + '. ' + question.questionTitle);
        $('#optionA').val(question.optionA).next('label').text(question.optionA);
        $('#optionB').val(question.optionB).next('label').text(question.optionB);
        $('#optionC').val(question.optionC).next('label').text(question.optionC);
        $('#optionD').val(question.optionD).next('label').text(question.optionD);
        $('#correctAnswerHidden').val(question.correctAnswer);
        $('input[name="selectedOption"]').prop('checked', false);
    }

    $('#submitBtn').click(function () {
        var selectedOption = $('input[name="selectedOption"]:checked').val();
        if (!selectedOption) {
            showToast('Please select an option before submitting!');
            return;
        }
        var data = {
            question: $('#questionTitle').text(),
            selectedOption: selectedOption,
            correctAnswer: $('#correctAnswerHidden').val()
        };

        $.ajax({
            url: '/mock/test/saveAnswer',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                // HTTP 200 reached — proceed with redirect
                window.location.href = '/mock/test/submit';
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
                showToast('Failed to Submit answer. Please try again!');
                toggleLoader();
            }
        });
    });
});