document.addEventListener("DOMContentLoaded", function () {
    var sidebarToggles = document.querySelectorAll(".js-sidebar-toggle");
    var sidebarClosers = document.querySelectorAll(".js-sidebar-close");

    function setSidebar(open) {
        document.body.classList.toggle("sidebar-open", open);
        sidebarToggles.forEach(function (button) {
            button.setAttribute("aria-expanded", open ? "true" : "false");
        });
    }

    sidebarToggles.forEach(function (button) {
        button.addEventListener("click", function () {
            setSidebar(!document.body.classList.contains("sidebar-open"));
        });
    });

    sidebarClosers.forEach(function (button) {
        button.addEventListener("click", function () {
            setSidebar(false);
        });
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            setSidebar(false);
        }
    });

    document.querySelectorAll(".sidebar-link").forEach(function (link) {
        link.addEventListener("click", function () {
            setSidebar(false);
        });
    });

    var pendingConfirmForm = null;
    var confirmModalEl = document.getElementById("appConfirmModal");
    var confirmMessageEl = confirmModalEl ? confirmModalEl.querySelector(".app-confirm-message") : null;
    var confirmSubmitButton = confirmModalEl ? confirmModalEl.querySelector(".js-app-confirm-submit") : null;
    var confirmModal = confirmModalEl && window.bootstrap ? new bootstrap.Modal(confirmModalEl) : null;

    document.querySelectorAll(".js-confirm").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (form.dataset.confirmed === "true") {
                delete form.dataset.confirmed;
                return;
            }
            event.preventDefault();
            pendingConfirmForm = form;
            if (confirmMessageEl) {
                confirmMessageEl.textContent = form.getAttribute("data-message") || "Lanjutkan aksi ini?";
            }
            if (confirmModal) {
                confirmModal.show();
                return;
            }
            form.dataset.confirmed = "true";
            form.requestSubmit();
        });
    });

    if (confirmSubmitButton) {
        confirmSubmitButton.addEventListener("click", function () {
            if (!pendingConfirmForm) {
                return;
            }
            var form = pendingConfirmForm;
            pendingConfirmForm = null;
            form.dataset.confirmed = "true";
            if (confirmModal) {
                confirmModal.hide();
            }
            form.requestSubmit();
        });
    }

    document.querySelectorAll(".js-editable-field").forEach(function (field) {
        var input = field.querySelector("[data-editable-control]");
        var button = field.querySelector(".js-field-edit");
        if (!input || !button) {
            return;
        }

        button.addEventListener("click", function () {
            input.removeAttribute("readonly");
            field.classList.add("is-editing");
            input.focus();
            if (typeof input.select === "function") {
                input.select();
            }
        });
    });

    document.querySelectorAll(".js-auth-form").forEach(function (form) {
        var errorEl = form.querySelector(".js-auth-error");
        var email = form.querySelector("input[name='email']");
        var password = form.querySelector("input[name='password']");

        function showAuthError(message, target) {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = message;
            errorEl.classList.remove("d-none");
            if (target) {
                target.focus();
            }
        }

        function clearAuthError() {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = "";
            errorEl.classList.add("d-none");
        }

        [email, password].forEach(function (input) {
            if (input) {
                input.addEventListener("input", clearAuthError);
            }
        });

        form.addEventListener("submit", function (event) {
            clearAuthError();
            if (!email || !email.value.trim()) {
                event.preventDefault();
                showAuthError("Email wajib diisi.", email);
                return;
            }
            if (!email.checkValidity()) {
                event.preventDefault();
                showAuthError("Format email tidak valid. Gunakan format nama@email.com.", email);
                return;
            }
            if (!password || !password.value.trim()) {
                event.preventDefault();
                showAuthError("Password wajib diisi.", password);
            }
        });
    });

    document.querySelectorAll(".js-payment-form").forEach(function (form) {
        var errorEl = form.querySelector(".js-payment-error");
        var proofInput = form.querySelector("input[name='buktiPembayaran']");
        var maxSize = 5 * 1024 * 1024;
        var allowedExtensions = ["pdf", "png", "svg", "jpg", "jpeg"];

        function showPaymentError(message, target) {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = message;
            errorEl.classList.remove("d-none");
            if (target) {
                target.focus();
            }
        }

        function clearPaymentError() {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = "";
            errorEl.classList.add("d-none");
        }

        form.querySelectorAll("input").forEach(function (input) {
            input.addEventListener("change", clearPaymentError);
            input.addEventListener("input", clearPaymentError);
        });

        form.addEventListener("submit", function (event) {
            clearPaymentError();
            var selectedMethod = form.querySelector("input[name='metodePembayaran']:checked");
            if (!selectedMethod) {
                event.preventDefault();
                showPaymentError("Metode pembayaran wajib dipilih.", form.querySelector("input[name='metodePembayaran']"));
                return;
            }
            var file = proofInput && proofInput.files ? proofInput.files[0] : null;
            if (!file) {
                event.preventDefault();
                showPaymentError("Bukti pembayaran wajib diunggah.", proofInput);
                return;
            }
            var extension = file.name.split(".").pop().toLowerCase();
            if (allowedExtensions.indexOf(extension) === -1) {
                event.preventDefault();
                showPaymentError("Format bukti pembayaran harus PDF, PNG, SVG, JPG, atau JPEG.", proofInput);
                return;
            }
            if (file.size > maxSize) {
                event.preventDefault();
                showPaymentError("Ukuran bukti pembayaran maksimal 5MB.", proofInput);
            }
        });
    });

    document.querySelectorAll(".js-booking-form").forEach(function (form) {
        var start = form.querySelector(".js-date-start");
        var end = form.querySelector(".js-date-end");
        var durationEl = form.querySelector(".js-duration");
        var totalEl = form.querySelector(".js-total");
        var errorEl = form.querySelector(".js-booking-error");
        var price = Number(form.getAttribute("data-price") || 0);
        var formatter = new Intl.NumberFormat("id-ID", {
            style: "currency",
            currency: "IDR",
            maximumFractionDigits: 0
        });

        function updateEstimate() {
            var startDate = start.value ? new Date(start.value) : null;
            var endDate = end.value ? new Date(end.value) : null;
            var days = 0;
            if (startDate && endDate && endDate > startDate) {
                days = Math.max(1, Math.round((endDate - startDate) / 86400000));
            }
            durationEl.textContent = days;
            totalEl.textContent = formatter.format(days * price).replace("IDR", "Rp");
        }

        function showBookingError(message) {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = message;
            errorEl.classList.remove("d-none");
        }

        function clearBookingError() {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = "";
            errorEl.classList.add("d-none");
        }

        start.addEventListener("change", updateEstimate);
        end.addEventListener("change", updateEstimate);
        start.addEventListener("input", clearBookingError);
        end.addEventListener("input", clearBookingError);
        form.addEventListener("submit", function (event) {
            clearBookingError();
            if (!start.value) {
                event.preventDefault();
                showBookingError("Tanggal sewa wajib diisi.");
                start.focus();
                return;
            }
            if (!end.value) {
                event.preventDefault();
                showBookingError("Tanggal kembali wajib diisi.");
                end.focus();
                return;
            }
            if (new Date(end.value) <= new Date(start.value)) {
                event.preventDefault();
                showBookingError("Tanggal kembali harus setelah tanggal sewa.");
                end.focus();
            }
        });
        updateEstimate();
    });

    document.querySelectorAll(".js-car-form").forEach(function (form) {
        var input = form.querySelector(".js-car-image-input");
        var dropzone = form.querySelector(".js-car-dropzone");
        var preview = form.querySelector(".js-car-preview");
        var previewWrap = form.querySelector(".js-car-preview-wrap");
        var uploadActions = form.querySelector(".js-car-upload-actions");
        var errorEl = form.querySelector(".js-car-upload-error");
        var maxSize = 2 * 1024 * 1024;
        var allowedTypes = ["image/jpeg", "image/png", "image/gif"];
        var allowedExtensions = ["jpg", "jpeg", "png", "gif"];

        function showUploadError(message) {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = message;
            errorEl.classList.remove("d-none");
        }

        function clearUploadError() {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = "";
            errorEl.classList.add("d-none");
        }

        function isAllowed(file) {
            var extension = file.name.split(".").pop().toLowerCase();
            return allowedTypes.indexOf(file.type) !== -1 && allowedExtensions.indexOf(extension) !== -1;
        }

        function handleFile(file) {
            clearUploadError();
            if (!file) {
                return false;
            }
            if (!isAllowed(file)) {
                showUploadError("Format gambar harus JPG, JPEG, PNG, atau GIF.");
                input.value = "";
                if (previewWrap) {
                    previewWrap.classList.add("d-none");
                }
                if (uploadActions) {
                    uploadActions.classList.add("d-none");
                }
                return false;
            }
            if (file.size > maxSize) {
                showUploadError("Ukuran gambar maksimal 2MB.");
                input.value = "";
                if (previewWrap) {
                    previewWrap.classList.add("d-none");
                }
                if (uploadActions) {
                    uploadActions.classList.add("d-none");
                }
                return false;
            }
            if (preview) {
                preview.src = URL.createObjectURL(file);
            }
            if (previewWrap) {
                previewWrap.classList.remove("d-none");
            }
            if (uploadActions) {
                uploadActions.classList.remove("d-none");
            }
            return true;
        }

        function assignSingleFile(file) {
            try {
                var transfer = new DataTransfer();
                transfer.items.add(file);
                input.files = transfer.files;
            } catch (error) {
                showUploadError("Browser tidak dapat membaca file drag and drop. Klik area unggah untuk memilih file.");
            }
        }

        if (!input || !dropzone) {
            return;
        }

        input.addEventListener("change", function () {
            handleFile(input.files[0]);
        });

        ["dragenter", "dragover"].forEach(function (eventName) {
            dropzone.addEventListener(eventName, function (event) {
                event.preventDefault();
                dropzone.classList.add("is-dragover");
            });
        });

        ["dragleave", "drop"].forEach(function (eventName) {
            dropzone.addEventListener(eventName, function (event) {
                event.preventDefault();
                dropzone.classList.remove("is-dragover");
            });
        });

        dropzone.addEventListener("drop", function (event) {
            var file = event.dataTransfer.files[0];
            if (handleFile(file)) {
                assignSingleFile(file);
            }
        });
    });

    document.querySelectorAll(".js-profile-form").forEach(function (form) {
        var input = form.querySelector(".js-profile-image-input");
        var preview = form.querySelector(".js-profile-preview");
        var initial = form.querySelector(".js-profile-initial");
        var errorEl = form.querySelector(".js-profile-upload-error");
        var maxSize = 2 * 1024 * 1024;
        var allowedTypes = ["image/jpeg", "image/png"];
        var allowedExtensions = ["jpg", "jpeg", "png"];

        function showProfileError(message) {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = message;
            errorEl.classList.remove("d-none");
        }

        function clearProfileError() {
            if (!errorEl) {
                return;
            }
            errorEl.textContent = "";
            errorEl.classList.add("d-none");
        }

        function isAllowedProfileImage(file) {
            var extension = file.name.split(".").pop().toLowerCase();
            return allowedTypes.indexOf(file.type) !== -1 && allowedExtensions.indexOf(extension) !== -1;
        }

        if (!input) {
            return;
        }

        input.addEventListener("change", function () {
            clearProfileError();
            var file = input.files[0];
            if (!file) {
                return;
            }
            if (!isAllowedProfileImage(file)) {
                showProfileError("Format foto profil harus JPG atau PNG.");
                input.value = "";
                return;
            }
            if (file.size > maxSize) {
                showProfileError("Ukuran foto profil maksimal 2MB.");
                input.value = "";
                return;
            }
            if (preview) {
                preview.src = URL.createObjectURL(file);
                preview.classList.remove("d-none");
            }
            if (initial) {
                initial.classList.add("d-none");
            }
        });
    });
});
