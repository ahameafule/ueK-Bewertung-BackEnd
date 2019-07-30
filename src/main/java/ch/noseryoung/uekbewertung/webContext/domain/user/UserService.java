package ch.noseryoung.uekbewertung.webContext.domain.user;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ch.noseryoung.uekbewertung.webContext.domain.role.Role;
import ch.noseryoung.uekbewertung.webContext.domain.user.dto.UserDTO;
import ch.noseryoung.uekbewertung.webContext.domain.user.dto.UserMapper;

/**
 * This class implements all data access related methods targeted towards the
 * entity user.
 * 
 * @author lohse
 *
 */
@Service
public class UserService implements UserDetailsService {

	private UserRepository userRepository;

	private BCryptPasswordEncoder bCryptPasswordEncoder;
	


	/**
	 * @param userRepository
	 */
	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User could not be found");
		}
		return new UserDetailsImpl(user);
	}
	
	public User findByUsername(String name) {
		User user = ((UserRepository) userRepository).findByLastName(name);
		return user;
	}
	
	public void deleteByUsername(String name) {
		((UserRepository) userRepository).deleteByLastName(name);
	}

	public Optional<User> findById(Long id) {
		Optional<User> user = userRepository.findById(id);
		return user;
	}

	/**
	 * returns all Users
	 */
	public List<User> findAll() {
		List<User> users = userRepository.findAll();
		return users;
	}
	
	
	public List<User> findAllByOrderByJoinYear() {
		List<User> users = userRepository.findAllByOrderByJoinYearDesc();
		return users;
	}
	
	public List<User> findAllApprentices() {
		Set<Role> roles = new HashSet<Role>();
		roles.add(new Role(2L, "USER"));
		List<User> users = userRepository.findAllByRoles(roles);
		return users;
	}
	
	public List<User> findAllCourseLeaders() {
		Set<Role> roles = new HashSet<Role>();
		roles.add(new Role(1L, "ADMIN"));
		List<User> users = userRepository.findAllByRoles(roles);
		return users;
	}

	/**
	 * tells the repository where to create a user
	 * @param User
	 */
	public void save(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setCreationdate(new Date());
		userRepository.save(user);
	}
	
	/**
	 * tells the repository where to create multiple users
	 * @param User
	 */
	public void save(List<User> users) {
		userRepository.saveAll(users);
	}

	/**
	 * updates the givin user defined with id 
	 * @param newUser
	 * @param id
	 * @throws NoSuchElementException
	 */
	public void update(User newUser, Long id) throws NoSuchElementException {
		Optional<User> currentUser = userRepository.findById(id);
		if (currentUser.isPresent()) {
			newUser.setId(id);
			userRepository.save(newUser);
		} else {
			throw new NoSuchElementException(String.format("No user with given id '%d' found", id));
		}
	}

	/**
	 * Tells the repository where to delete the user
	 * @param id
	 */
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}
	
	/**
	 * deletes users after one year
	 * @param id
	 * @return
	 */
	public void deleteOldUsers(Date dateToCheck) {
		
		List<User> users = userRepository.findAll();
		
		for (User user : users) {
			boolean isTrainer = false;
			
			for (Role role : user.getRoles()) {
				/**
				 * The user will not be deleted if he has the role "trainer"
				 */
				if(role.getName().equals("trainer")) {
					isTrainer = true;
				}
			}
			
			if(!isTrainer) {
				if(user.getCreationdate() != null && user.getCreationdate().before(dateToCheck)) {
					userRepository.delete(user);
				}
			}
		}
	}
	
}
