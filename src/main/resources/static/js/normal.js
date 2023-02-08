$(document).ready(() => {
  // close modal dialog
  $('body').on('click', '.modal-header [data-bs-dismiss=modal]', function () {
    $('#staticBackdrop').css('display', 'none');
  }).on('click', '.btn--download', function (e) {
    e.preventDefault();
    if ($(this).attr('href') === '/login') {
      // show modal dialog
      const html = `
                <div class="modal fade show" id="staticBackdrop" data-bs-backdrop="static"
                     data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" style="display: block;"
                     aria-modal="true" role="dialog">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5" id="exampleModalCenterTitle">
                                    <i data-feather="alert-triangle" class="text-danger"></i>
                                    <span>Required</span></h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <p>You need <a href="/login">Login</a> to download this document.</p>
                            </div>
                        </div>
                    </div>
                </div>
            `;
      if ($('.modal-dialog').length === 0) {
        $(this).after(html);
        $('[data-feather=alert-triangle]').replaceWith(
            feather.icons['alert-triangle'].toSvg());
      } else {
        $(this).next().css('display', 'block');
      }
    }
    if ($(this).data('v-required') === 'download') {
      if (confirm('Are you sure?')) {
        const href = $(this).attr('href');
        window.location.href = href;
      }
    }
  });
});