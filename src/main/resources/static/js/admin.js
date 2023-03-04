$(document).ready(() => {
    // tooltip hover
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl, {
        html: true,
    }));

    $('input[type=search]').keyup(function () {
        if (!$(this).val().startsWith(' ')) {
            return $(this).val()
        } else {
            $(this).val('')
        }
    });

    // confirm before
    $('[data-v-delete]').on('click', function () {
        return confirm('Do you want delete: ' + $(this).data('v-delete'));
    })
    $('[data-v-reset]').on('click', function () {
        return confirm('Do you want reset password: ' + $(this).data('v-reset'));
    })
    $('#main-form').one('submit', function (e) {
        e.preventDefault();
        if (confirm('Do you sure to delete many products?')) {
            $(this).submit();
        }
    })
    // current url
    if ((location.pathname.split("/")[2]) !== "") {
        $('.navbar-nav .nav-item a[href^="/admin/' + location.pathname.split("/")[2] + '"]').addClass('active');
    }
    // close modal dialog
    $('body').on('click', '.modal-header [data-bs-dismiss=modal]', function () {
        $('#staticBackdrop').css('display', 'none');
    })
});