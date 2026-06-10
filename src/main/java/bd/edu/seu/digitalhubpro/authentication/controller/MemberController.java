package bd.edu.seu.digitalhubpro.authentication.controller;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.service.FileUploadService;
import bd.edu.seu.digitalhubpro.authentication.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping({"/member-list"})
    public String home(@RequestParam(value = "name", defaultValue = "") String name, Model model) {
        List<Member> memberList = this.memberService.getAllMembers();
        model.addAttribute("memberList", memberList);
        return "authentication/memberList";
    }

    @GetMapping({"/register"})
    public String register(Model model) {
        Member member = new Member();
        model.addAttribute("members", member);
        model.addAttribute("title", "Create New Account");
        return "authentication/addMember";
    }

    @PostMapping({"/register"})
    public String save(@ModelAttribute("members") @Valid Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()) {
            return "authentication/addMember";
        } else {
            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                member.setPhoto(fileName);
                member.setRoles(new ArrayList<>());
                member.getRoles().add("ROLE_STUDENT");
                Member savedMember = this.memberService.save(member);
                String uploadDir = "member-photos/" + savedMember.getId();
                FileUploadService.saveFile(uploadDir, fileName, multipartFile);
            }

            redirectAttributes.addFlashAttribute("message", "Customer saved successfully");

            // ✅ THIK KORA HOLO: Pura member list-e na pathiye login page-e redirect hobe
            return "redirect:/login?register=true";
        }
    }

    @GetMapping({"/member/{id}"})
    public String customer(@PathVariable String id, Model model) {
        Optional<Member> member = this.memberService.findById(id);
        member.ifPresent((value) -> model.addAttribute("members", value));
        return "authentication/showMembers";
    }

    @GetMapping({"/member/{id}/edit"})
    public String edit(@PathVariable String id, Model model) {
        Optional<Member> memberOptional = this.memberService.findById(id);
        if (memberOptional.isPresent()) {
            Member member = (Member) memberOptional.get();
            model.addAttribute("members", member);
            model.addAttribute("title", "Edit Member");
            return "authentication/addMember";
        } else {
            model.addAttribute("errorMessage", "Member not found ! Add New Member");
            return "redirect:/register";
        }
    }

    @PostMapping({"/member/{id}/edit"})
    public String updateMember(@PathVariable String id, @ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        Optional<Member> existingMember = this.memberService.findById(id);
        if (existingMember.isPresent()) {
            member.setId(id);
            this.memberService.updateMember(member);
            redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully!");
            return "redirect:/member-list";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Member to update not found!");
            return "redirect:/register";
        }
    }

    @GetMapping({"/member/{id}/delete"})
    public String delete(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        this.memberService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Customer deleted successfully");
        return "redirect:/member-list";
    }
}