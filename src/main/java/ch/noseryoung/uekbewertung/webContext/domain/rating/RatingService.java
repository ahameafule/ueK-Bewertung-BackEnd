package ch.noseryoung.uekbewertung.webContext.domain.rating;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ch.noseryoung.uekbewertung.config.UUIDGenerator;
import ch.noseryoung.uekbewertung.webContext.domain.mailsending.MailSender;
import ch.noseryoung.uekbewertung.webContext.domain.user.User;

/**
 * This class implements all data access related methods targeted towards the
 * entity rating.
 * 
 * @author Joel Ahameafule
 *
 */
@Service
public class RatingService {

	private RatingRepository ratingRepository;
	private MailSender mailSender;

	/**
	 * @param ratingRepository
	 */
	@Autowired
	public RatingService(RatingRepository ratingRepository, MailSender mailSender) {
		this.ratingRepository = ratingRepository;
		this.mailSender = mailSender;
	}

	/**
	 * find course by the giving id
	 * @param id
	 * @return
	 */
	public Optional<Rating> findById(Long id) {
		Optional<Rating> rating = ratingRepository.findById(id);
		return rating;
	}
	
	public Optional<Rating> findByUUID(String uuid) {
		return ratingRepository.findByuuid(uuid);
	}
	
	public Optional<Rating> findByUser(User user) {
		return ratingRepository.findByUser(user);
	}

	/**
	 * find all ratings
	 * @return
	 */
	public List<Rating> findAll() {
		List<Rating> ratings = ratingRepository.findAll();
		return ratings;
	}
	
	/**
	 * find all ratings ordered
	 * @return
	 */
	public List<Rating> findAllOrdered() {
		List<Rating> ratings = ratingRepository.findAllByOrderByCourseCourseNumberAscCourseCourseLeadAsc();
		return ratings;
	}

	/**
	 * saves the rating
	 * @param rating
	 */
	public boolean save(Rating rating) {
		List<Rating> currentRating = ratingRepository.findByCourseAndUser(rating.getCourse(), rating.getUser());
	
		if (currentRating.isEmpty()) {
			try {
				rating.setUUID(UUIDGenerator.generateUUID());
				ratingRepository.save(rating);
				if (rating.getUser().getEmail() != null && !("undefined").equals(rating.getUser().getEmail())) {
					mailSender.sendEmail(rating.getUser().getEmail(), rating.getUUID());
				}
				return true;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return false;
	}
	
	/**
	 * 
	 * @param rating
	 */
	public void saveAll(List<Rating> ratings) {
		for (Rating rating : ratings) {	
			try {
				rating.setUUID(UUIDGenerator.generateUUID());
				if (rating.getUser().getEmail() != null) {
					mailSender.sendEmail(rating.getUser().getEmail(), rating.getUUID());
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		ratingRepository.saveAll(ratings);
	}

	/**
	 * updates the given rating defined with id 
	 * @param new Rating
	 * @param id
	 * @throws NoSuchElementException
	 */
	public void update(Rating newRating, Long id) throws NoSuchElementException {
		Optional<Rating> currentRating = ratingRepository.findById(id);
		if (currentRating.isPresent()) {
			newRating.setId(id);
			ratingRepository.save(newRating);
		} else {
			throw new NoSuchElementException(String.format("No rating with given id '%d' found", id));
		}
	}
	
	/**
	 * 
	 * @param newRating
	 * @param id
	 * @throws NoSuchElementException
	 */
	public void updateByUUID(Rating newRating, String uuid) throws NoSuchElementException {
		Optional<Rating> currentRating = ratingRepository.findByuuid(uuid);
		if (currentRating.isPresent()) {
			newRating.setUUID(uuid);
			ratingRepository.save(newRating);
		} else {
			throw new NoSuchElementException(String.format("No rating with given uuid '%s' found", uuid));
		}
	}

	public void deleteById(Long id) {
		ratingRepository.deleteById(id);
	}
}