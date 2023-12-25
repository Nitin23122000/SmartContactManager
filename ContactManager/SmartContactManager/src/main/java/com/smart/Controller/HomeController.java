package com.smart.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Entity.User;
import com.smart.helperMessage.Message;
import com.smart.repository.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userrepository;
	
	@GetMapping("/")
	public String Home(Model model) {
		
		model.addAttribute("title","Home- Smart Contact Manager");
		System.out.println("Inside Home Handler");
		return "home";
	}
	
	@GetMapping("/about")
	public String About(Model model) {
		
		model.addAttribute("title", "About- Smart Contact Manager");
		System.out.println("Inside about handler");
		return "about";
	}
	
	@GetMapping("/signup")
	public String SuignUp(Model model){
		
		model.addAttribute("title", "Signup- Smart Contact Manager");
		model.addAttribute("user",new User());
		System.out.println("Inside SignUp Handler");
		return "signup";
	}
	
	//handler for registration for user
	@RequestMapping(value = "/do_register",  method = RequestMethod.POST)
	public String registerUser(@Valid  @ModelAttribute("user") User user, BindingResult result1, @RequestParam(value="aggrement" ,defaultValue = "false") boolean aggrement,Model model,  HttpSession session) {
		
		try {
			
			if(!aggrement) {
				
				System.out.println("You Have Not Agreed The Terms And Condition");
				throw new Exception("You Have Not Agreed The Terms And Condition");
			}
			
			
			if(result1.hasErrors()) {
				
				System.out.println("error"+ result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Aggrement"+aggrement);
			System.out.println("User: "+user);
			
			User Result = this.userrepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Succeessfully Registered" , "alert-success"));

			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong!!" + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	@GetMapping("/signin")
  public String Login(Model model) {
	
		System.out.println("Inside Login handler");
		model.addAttribute("title", "Login -Smart Contact Manager");
		return "login";
	
}
//	@GetMapping("/login-fail")
//	public String LoginFail(Model model) {
//		
//		System.out.println("Inside Failure Handler");
//		model.addAttribute("title", "Login Fail");
//		return "login-fail";
//	}
}
