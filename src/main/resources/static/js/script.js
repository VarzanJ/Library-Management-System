document.addEventListener('DOMContentLoaded', function () {

    // 1. Confirm dialog for anything with data-confirm="message"
    document.querySelectorAll('[data-confirm]').forEach(function (el) {
        var eventName = el.tagName === 'FORM' ? 'submit' : 'click';
        el.addEventListener(eventName, function (e) {
            if (!window.confirm(el.getAttribute('data-confirm'))) {
                e.preventDefault();
            }
        });
    });

    // 2. Stop double submission (disable the submit button after the first click)
    document.querySelectorAll('form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (e.defaultPrevented) { return; }
            var btn = form.querySelector('button[type="submit"]');
            if (btn) { setTimeout(function () { btn.disabled = true; }, 0); }
        });
    });
    window.addEventListener('pageshow', function () {
        document.querySelectorAll('button[type="submit"]').forEach(function (b) { b.disabled = false; });
    });

    // 3. Highlight the current page in the navigation bar
    var path = window.location.pathname;
    document.querySelectorAll('nav a').forEach(function (link) {
        var href = link.getAttribute('href');
        if (path === href || path.indexOf(href + '/') === 0) {
            link.classList.add('active');
        }
    });

    // 4. Show / hide password
    document.querySelectorAll('[data-toggle-password]').forEach(function (box) {
        box.addEventListener('change', function () {
            var field = document.querySelector(box.getAttribute('data-toggle-password'));
            if (field) { field.type = box.checked ? 'text' : 'password'; }
        });
    });

    // 5. Hide success messages after a few seconds
    document.querySelectorAll('.success').forEach(function (msg) {
        setTimeout(function () { msg.style.display = 'none'; }, 5000);
    });
});
