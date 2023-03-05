package com.vuntfx17196.controller;

import com.vuntfx17196.dto.UserRegisterDTO;
import com.vuntfx17196.global.AccountResourceException;
import com.vuntfx17196.model.KeyAndPasswordVM;
import com.vuntfx17196.model.User;
import com.vuntfx17196.security.RandomUtil;
import com.vuntfx17196.service.MailService;
import com.vuntfx17196.service.UserService;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

  private final UserService userService;
  private final MailService mailService;
  public static final String REGEX_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

  public LoginController(UserService userService, MailService mailService) {
    this.userService = userService;
    this.mailService = mailService;
  }

  @GetMapping("/login")
  public String login() {
    // kiem tra trang thai dang nhap cua client
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      return "login";
    }

    return "redirect:/";
  }

  @GetMapping("/register")
  public String registerGet(Model model) {
    // tao doi tuong userDTO de luu du lieu cua form
    model.addAttribute("user", new UserRegisterDTO());
    return "register";
  }

  @PostMapping("/register")
  public String registerPost(@Valid @ModelAttribute("user") UserRegisterDTO userModel,
      BindingResult bindingResult, Model model) {

    // check email already exists
    User existingUser = userService.getUserByEmail(userModel.getEmail());
    if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail()
        .isEmpty()) {
      bindingResult.rejectValue("email", null,
          "There is already an account registered with the same email");
    }

    // xu ly loi form input
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", userModel);
      return "register";
    }
    String password = (RandomUtil.generatePassword());
    User user = userService.registerUser(userModel, password);
    mailService.sendActivationEmail(user, password);
    model.addAttribute("userRegisterSuccess", "Now you can check your mailbox for new password.");
    return "register";
  }

  @GetMapping("/forgotpassword")
  public String requestPasswordReset(Model model) {
    model.addAttribute("email");
    return "forgotpassword";
  }

  @PostMapping("/forgotpassword")
  public String requestPasswordReset(@RequestParam String email, Model model) {
    boolean isValidEmail = patternMatches(email, REGEX_PATTERN);
    String errorModel = "";
    String message = "";
    if (!isValidEmail) {
      errorModel = "resetPassword";
      message = "Email type is not valid.";
    } else {
      Optional<User> user = userService.requestPasswordReset(email);
      if (user.isPresent()) {
        mailService.sendPasswordResetMail(user.get());
        errorModel = "checkMailForResetPassword";
        message = "A link was send to your email. Please check your mailbox and follow next step.";
      } else {
        // Pretend the request has been successful to prevent checking which emails really exist
        // but log that an invalid attempt has been made
        errorModel = "resetPassword";
        message = "Password reset requested for non existing mail.";
      }
    }
    model.addAttribute(errorModel, message);
    return "forgotpassword";
  }

  @GetMapping("/account/reset/finish")
  public String finishPasswordReset(@RequestParam(value = "key") String key, Model model) {
    KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
    keyAndPasswordVM.setKey(key);
    model.addAttribute("keyAndPasswordVM", keyAndPasswordVM);
    return "passwordReset";
  }

  @PostMapping("/account/reset/finish")
  public String finishPasswordReset(
      @Valid @ModelAttribute("keyAndPasswordVM") KeyAndPasswordVM keyAndPassword,
      BindingResult bindingResult, Model model) {
    String errorModel = "";
    String message = "";
    if (bindingResult.hasErrors()) {
      errorModel = "keyAndPasswordVM";
      model.addAttribute(errorModel, keyAndPassword);
    } else {
      Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(),
          keyAndPassword.getKey());

      if (user.isEmpty()) {
        throw new AccountResourceException("No user was found for this reset key");
      }
      errorModel = "completeResetPassword";
      message = "New password was set. Now you can try login";
      model.addAttribute(errorModel, message);
    }
    return "passwordReset";
  }

  /**
   * Check valid email type
   *
   * @param emailAddress email address
   * @param regexPattern regex to pattern
   * @return true if valid, otherwise false
   */
  public static boolean patternMatches(String emailAddress, String regexPattern) {
    return Pattern.compile(regexPattern).matcher(emailAddress).matches();
  }

}
