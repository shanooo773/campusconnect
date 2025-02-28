package com.insta.instagram.service;

import  com.insta.instagram.details.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.insta.instagram.exceptions.UserException;
import com.insta.instagram.modal.User;
import com.insta.instagram.repository.UserRepository;
import com.insta.instagram.security.JwtTokenClaims;
import com.insta.instagram.security.JwtTokenProvider;

@Service
public class UserServiceImplementation implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private JwtTokenProvider jwtTokenProvider;
	
	public UserServiceImplementation(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

	@Override
	public User updateUserDetails(User updatedUser, User existingUser) throws UserException {
		
		if(updatedUser.getName() != null) {
			existingUser.setName(updatedUser.getName());
		}
		
		if(updatedUser.getUsername() != null) {
			existingUser.setUsername(updatedUser.getUsername());
		}
		
		if(updatedUser.getEmail() != null) {
			existingUser.setEmail(updatedUser.getEmail());
		}
		if(updatedUser.getBio() != null) {
			existingUser.setBio(updatedUser.getBio());
		}
		
		if(updatedUser.getGender() != null) {
			existingUser.setGender(updatedUser.getGender());
		}
		
		if(updatedUser.getImage() != null) {
			existingUser.setImage(updatedUser.getImage());
		}
		
		if(updatedUser.getMobile() != null) {
			existingUser.setMobile(updatedUser.getMobile());
		}
		
		if(updatedUser.getWebsite() != null) {
			existingUser.setWebsite(updatedUser.getWebsite());
		}
		
		if(updatedUser.getProfileImage() != null) {
			existingUser.setProfileImage(updatedUser.getProfileImage());
		}
		
		
		if(updatedUser.getId() == existingUser.getId()) {
			
			return userRepository.save(existingUser);
			
		}
		
		throw new UserException("You can't update this user");
		
		
		
	}

	@Override
	public User registerUser(User user) throws UserException {
		
		Optional<User> isEmailExist = userRepository.findByEmail(user.getEmail());
		
		if(isEmailExist.isPresent()) {
			throw new UserException("Email is already exist. Use different email");
		}
		
		Optional<User> isUsernameExist = userRepository.findByUsername(user.getUsername());
		
		if(isUsernameExist.isPresent()) {
			throw new UserException("User name is already exist. Try different user name");
		}
		
		if(user.getEmail() == null || user.getPassword() == null || user.getUsername() == null || user.getName() == null) {
			throw new UserException("All attributes are empty!!");
		}
		
		User newUser = new User();
		newUser.setEmail(user.getEmail());
		newUser.setPassword(user.getPassword());
		newUser.setUsername(user.getUsername());
		newUser.setName(user.getName());
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));
		
		return userRepository.save(newUser);
	}

	

	@Override
	public User findUserById(int userId) throws UserException {
		Optional<User> opt = userRepository.findById(userId);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new UserException("User not exist with id " + userId);
	}
	
	@Override
	public String followUser(int reqUserId, int followUserId) throws UserException {
		
		User reqUser = findUserById(reqUserId);
		User followUser = findUserById(followUserId);
		
		UserDetails follower = new UserDetails();
		follower.setId(reqUser.getId());
		follower.setName(reqUser.getName());
		follower.setUsername(reqUser.getUsername());
		follower.setEmail(reqUser.getEmail());
		follower.setUserImage(reqUser.getImage());
				
		UserDetails following = new UserDetails();
		following.setId(followUser.getId());
		following.setName(followUser.getName());
		following.setUsername(followUser.getUsername());
		following.setEmail(followUser.getEmail());
		following.setUserImage(followUser.getImage());
		
		reqUser.getFollowing().add(following);
		followUser.getFollower().add(follower);
		
		userRepository.save(followUser);
		userRepository.save(reqUser);
		
		return "You are following "+ followUser.getUsername();
	}

	@Override
	public List<User> searchUser(String query) throws UserException {
		List<User> users = userRepository.findByQuery(query);
		if(users.size() == 0) {
			throw new UserException("User not found");
		}
		return users;
	}

	@Override
	public User findUserProfile(String token) throws UserException {
		token=token.substring(7);
		JwtTokenClaims jwtTokenClaims = jwtTokenProvider.getClaimsFromToken(token);
		 String email = jwtTokenClaims.getUsername();
		 Optional<User> opt=userRepository.findByEmail(email);
		 
		 if(opt.isPresent()) {
			 return opt.get();
		 }
		 throw new UserException("Invalid token");
	}

	@Override
	public User findUserByUsername(String username) throws UserException {
		Optional <User> opt = userRepository.findByUsername(username);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new UserException("User not exist " + username);
	}

	@Override
	public String unfollowUser(int reqUserId, int followUserId) throws UserException {
		User reqUser = findUserById(reqUserId);
		User followUser = findUserById(followUserId);
		
		UserDetails follower = new UserDetails();
		
		follower.setId(reqUser.getId());
		follower.setName(reqUser.getName());
		follower.setUsername(reqUser.getUsername());
		follower.setEmail(reqUser.getEmail());
		follower.setUserImage(reqUser.getImage());
				
		UserDetails following = new UserDetails();
		
		following.setId(followUser.getId());
		following.setName(followUser.getName());
		following.setUsername(followUser.getUsername());
		following.setEmail(followUser.getEmail());
		following.setUserImage(followUser.getImage());
		
		reqUser.getFollowing().remove(following);
		followUser.getFollower().remove(follower);
		
		userRepository.save(followUser);
		userRepository.save(reqUser);
		return "You have unfollowed " + followUser.getUsername();
	}

	@Override
	public List<User> findUserByIds(List<Integer> userIds) throws UserException {
		
		List<User> users = userRepository.findAllUsersByUserIds(userIds);
		
		return users;
	}
	

	@Override
	public List<User> recommendUsers() {
        List<User> allUsers = userRepository.findAll();
        Collections.shuffle(allUsers);
        
        return allUsers.stream().limit(5).collect(Collectors.toList());
      }
	

}
