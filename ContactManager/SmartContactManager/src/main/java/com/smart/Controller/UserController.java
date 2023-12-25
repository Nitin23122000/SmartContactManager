package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Entity.Contact;
import com.smart.Entity.User;
import com.smart.helperMessage.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@ModelAttribute
	public void addcommonData(Model model,Principal principal) {
		
		String name = principal.getName();
		System.out.println("Username : "+name);
		User user = userRepository.getUserByUserName(name);
		System.out.println("User : "+user);
		
		System.out.println("inside dashboard handler");
		model.addAttribute("user",user);
		
	}

	
	@GetMapping("/dashboard")
	public String dashboard(Model model ,Principal principal) {
		
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	@GetMapping("/add-contactform")
	public String openAddContactForm(Model model) {
		
		System.out.println("Inside OpenAddContact Handler");
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add-contactform";
	}
	
	@PostMapping("/process-contact")
	public String submitContactForm(@ModelAttribute Contact contact, @RequestParam("imagefile") MultipartFile file, Principal principal,HttpSession session) {
		
		
		try {
			
			String name = principal.getName();
			
			User user = this.userRepository.getUserByUserName(name);
			
			if(file.isEmpty()) {
				
				contact.setImage("default.png");
				return "File is empty";
				
			}
			else {
				
				//save the name of image in database
				contact.setImage(file.getOriginalFilename());
				
				//orignally file will be saved here
				File savefile = new ClassPathResource("static/image").getFile();
				
				//path of the file where it uploaded
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
			}
			
			contact.setUser(user);
			
			user.getContacts().add(contact);
			
			this.userRepository.save(user);
			
			System.out.println("added to database");
			System.out.println("Data : "+contact);
			
			session.setAttribute("message", new Message("Contact Added Successfully!! ", "success") );
			
		} catch (Exception e) {
			System.out.println("Error : "+ e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something Went Wrong!!Try Again.. ", "alert") );

		}
		
		
		
		return "normal/add-contactform";
		
	}
	
	//per page =5[n]
	//current page = 0[page]
	@GetMapping("/view-contacts/{page}")
	public String viewContacts(@PathVariable("page") Integer page,Model model,Principal principal ) {
		
    	String userName = principal.getName();
		User User = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> Contacts = this.contactRepository.findContactsByUser(User.getId(),pageable);
		
		model.addAttribute("contacts",Contacts);
		model.addAttribute("currentpage",page);
		model.addAttribute("totalpages",Contacts.getTotalPages());
		
		System.out.println("Inside viewContacts handler");
		
		model.addAttribute("title","User All Contacts");
		return "normal/view-contacts";
	}
	
	@GetMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid,Model model, Principal principal) {
		
		System.out.println("Cid" + cid);
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		
		Contact contact = contactOptional.get();
		String loginUname = principal.getName();
		
		User user = this.userRepository.getUserByUserName(loginUname);
		model.addAttribute("title", loginUname);
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		
		
		return "normal/ShowContact_details";
	}
}
