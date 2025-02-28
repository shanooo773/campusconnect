package com.insta.instagram.service;

import java.util.List;

import com.insta.instagram.details.UserDetails;
import com.insta.instagram.exceptions.UserException;
import com.insta.instagram.modal.User;

public interface UserService {
	
	public User updateUserDetails(User updatedUser, User existingUser) throws UserException;
	public User registerUser(User user) throws UserException;
	public String followUser(int reqUserId, int followUserId) throws UserException;
	public User findUserById(int userId) throws UserException;
	public List<User> searchUser(String query) throws UserException;
	public User findUserProfile(String token) throws UserException;
	public User findUserByUsername(String userName) throws UserException;
	public String unfollowUser(int reqUserId, int followUserId) throws UserException;
	public List<User> findUserByIds(List<Integer> userIds) throws UserException;
	public List<User> recommendUsers() throws UserException;
	
	
		
	

}
