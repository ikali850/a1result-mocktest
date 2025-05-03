$(document).ready(function () {
    var currentIndex = document.getElementById("currentIndex").value;
    var totalQuestions = document.getElementById("totalQuestions").value;

    // method to show alert to change lang
    window.confirmLanguageChange = function () {
        let currentNumber = parseInt($("#currentIndex").val(), 10); // convert to number
        currentNumber++; // apply ++ operator

        if (currentIndex =='0') {
            // If incremented index is 1 or less, just submit
            $("#languageForm").submit();
            return;
        }

        // Otherwise, ask for confirmation
        if (confirm("Change language? Saved answers will be lost.")) {
            $("#languageForm").submit();
        }
    };


    function showToast(message) {
        document.getElementById('toastMessage').textContent = message;
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
        $("#previousBtn").prop("disabled", currentIndex <= 0);
    }

    function showLoader() {
        $('#loaderOverlay').show();
    }

    function hideLoader() {
        $('#loaderOverlay').hide();
    }

    $('#questionForm').on('submit', function (e) {
        e.preventDefault();
        showLoader();

        var selectedOption = $('input[name="selectedOption"]:checked').val();
        if (!selectedOption) {
            showToast('Please select an option!');
            hideLoader();
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
                    hideLoader();
                } else {
                    window.location.href = '/mock/test/submit';
                    hideLoader();
                }
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
                showToast('Failed to save answer. Please try again!');
                hideLoader();
            }
        });
    });

    $("#previousBtn").click(function () {
        showLoader();

        $.ajax({
            url: '/mock/test/prevQuestion',
            method: 'GET',
            success: function (response) {
                updateForm(response);
                currentIndex--;
                updateProgress();
                updateButtons();
                hideLoader();
                if (currentIndex < totalQuestions - 1) {
                    $('#nextBtn').show();
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching previous question:", error);
                showToast('Failed to load previous question!');
                hideLoader();
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

        if (!confirm('Are you sure you want to submit the test?')) {
            return;
        }

        showLoader();

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
                window.location.href = '/mock/test/submit';
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
                showToast('Failed to Submit answer. Please try again!');
                hideLoader();
            }
        });
    });
});
