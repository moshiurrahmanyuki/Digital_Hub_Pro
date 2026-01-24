
const form = document.getElementById('registrationForm');
const userTypeInput = document.getElementById('userType');
const nameInput = document.getElementById('name');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');
const userTypeGroup = document.getElementById('userTypeGroup');
const nameGroup = document.getElementById('nameGroup');
const emailGroup = document.getElementById('emailGroup');
const passwordGroup = document.getElementById('passwordGroup');
const confirmPasswordGroup = document.getElementById('confirmPasswordGroup');
const userTypeError = document.getElementById('userTypeError');
const nameError = document.getElementById('nameError');
const emailError = document.getElementById('emailError');
const passwordError = document.getElementById('passwordError');
const confirmPasswordError = document.getElementById('confirmPasswordError');

function validateUserType() {
    if (userTypeInput.value.trim() === '') {
        userTypeGroup.classList.add('error');
        userTypeError.textContent = 'User type is required';
        return false;
    } else {
        userTypeGroup.classList.remove('error');
        userTypeError.textContent = '';
        return true;
    }
}

function validateName() {
    if (nameInput.value.trim() === '') {
        nameGroup.classList.add('error');
        nameError.textContent = 'Name is required';
        return false;
    } else {
        nameGroup.classList.remove('error');
        nameError.textContent = '';
        return true;
    }
}

function validateEmail() {
    const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if (emailInput.value.trim() === '') {
        emailGroup.classList.add('error');
        emailError.textContent = 'Email is required';
        return false;
    } else if (!emailRegex.test(emailInput.value.trim())) {
        emailGroup.classList.add('error');
        emailError.textContent = 'Invalid email format';
        return false;
    } else {
        emailGroup.classList.remove('error');
        emailError.textContent = '';
        return true;
    }
}

function validatePassword() {
    if (passwordInput.value.trim() === '') {
        passwordGroup.classList.add('error');
        passwordError.textContent = 'Password is required';
        return false;
    } else if (passwordInput.value.trim().length < 8) {
        passwordGroup.classList.add('error');
        passwordError.textContent = 'Password must be at least 8 characters';
        return false;
    } else {
        passwordGroup.classList.remove('error');
        passwordError.textContent = '';
        return true;
    }
}

function validateConfirmPassword() {
    if (confirmPasswordInput.value.trim() === '') {
        confirmPasswordGroup.classList.add('error');
        confirmPasswordError.textContent = 'Confirm password is required';
        return false;
    } else if (confirmPasswordInput.value.trim() !== passwordInput.value.trim()) {
        confirmPasswordGroup.classList.add('error');
        confirmPasswordError.textContent = 'Passwords do not match';
        return false;
    } else {
        confirmPasswordGroup.classList.remove('error');
        confirmPasswordError.textContent = '';
        return true;
    }
}

userTypeInput.addEventListener('blur', validateUserType);
nameInput.addEventListener('blur', validateName);
emailInput.addEventListener('blur', validateEmail);
passwordInput.addEventListener('blur', validatePassword);
confirmPasswordInput.addEventListener('blur', validateConfirmPassword);

form.addEventListener('submit', (event) => {
    event.preventDefault();
    let isUserTypeValid = validateUserType();
    let isNameValid = validateName();
    let isEmailValid = validateEmail();
    let isPasswordValid = validatePassword();
    let isConfirmPasswordValid = validateConfirmPassword();

    if (isUserTypeValid && isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
        // Form is valid, submit the data
        // In a real application, you would send this data to a server
        console.log('Form submitted successfully!');
        console.log('User Type:', userTypeInput.value);
        console.log('Name:', nameInput.value);
        console.log('Email:', emailInput.value);
        console.log('Password:', passwordInput.value);
        //  form.submit(); // Uncomment this line to submit the form  <--  The fix is here
    } else {
        // Form is invalid, do not submit
        console.log('Form submission prevented due to validation errors.');
    }
});