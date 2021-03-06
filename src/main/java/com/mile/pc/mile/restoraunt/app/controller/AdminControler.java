package com.mile.pc.mile.restoraunt.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mile.pc.mile.restoraunt.app.dto_dao.RoleToUser;
import com.mile.pc.mile.restoraunt.app.repo.UserRepository;
import com.mile.pc.mile.restoraunt.app.service.AdminService;
import com.mile.pc.mile.restoraunt.app.service.CustomDTOservice;

@Controller
@RequestMapping("/admin")
public class AdminControler {

	@Autowired AdminService adminService;
	@Autowired UserRepository uRepo;
	@Autowired CustomDTOservice dto_ser;

	@GetMapping(path = "/users")
	public ModelAndView allUsers() {
		return new ModelAndView("admin/users-admin","users", dto_ser.usersDTO(uRepo.findAll()));
	}

	@GetMapping(path = "users/waiters")
	public ModelAndView getWaiters() {
		return new ModelAndView("admin/waiters-admin","waiters", dto_ser.usersDTO(adminService.getWaiters()));
	}

	@GetMapping(path = "/add-RTU-form")
	public ModelAndView addRoleToUserForm(@RequestParam long id) {
		return new ModelAndView("admin/add-role-to-user-admin", "rtu", 
				new RoleToUser(null,  uRepo.findById(id).get().getUsername()));
	}

	@GetMapping(path = "/remove-RFU-form")
	public ModelAndView removeRoleFromUser(@RequestParam long id) {
		return new ModelAndView("admin/remove-role-from-user-admin", "rfu", 
				new RoleToUser(null,  uRepo.findById(id).get().getUsername()));
	}

	@GetMapping(path = "/updateUser")
	public ModelAndView getUpdateOptions(@RequestParam long id) {
		return new ModelAndView("admin/update-admin","user", uRepo.findById(id).get());
	}

	/*
	 * Methods that change the DB
	 */

	@PostMapping(path = "/add-RTU")
	public String addRoleToUser(@ModelAttribute RoleToUser dto) {
		adminService.AddRoleToUser(dto.getUsername(), dto.getType());
		return "redirect:/admin/users";
	}

	@GetMapping(path = "/remove-RFU")
	public String removeRoleFromUser(@ModelAttribute RoleToUser dto) {
		adminService.removeRolefromUser(dto.getUsername(), dto.getType());
		return "redirect:/admin/users";
	}

	@GetMapping(path = "/deleteUser")
	public String deleteUser(@RequestParam long id) {
		adminService.removeUser(id);
		return "redirect:/admin/users";
	}

}
