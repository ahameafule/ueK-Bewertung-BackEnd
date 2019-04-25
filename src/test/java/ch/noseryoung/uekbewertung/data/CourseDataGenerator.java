/**
 * 
 */
package ch.noseryoung.uekbewertung.data;

import ch.noseryoung.uekbewertung.webContext.domain.course.Course;

/**
 * @author lohse
 *
 */
public class CourseDataGenerator implements DataGenerator<Course> {

	private LocationDataGenerator locationGenerator;
	private UserDataGenerator userGenerator;
	
	public CourseDataGenerator(LocationDataGenerator locationGenerator, UserDataGenerator userGenerator) {
		this.locationGenerator = locationGenerator;
		this.userGenerator = userGenerator;
	}

	@Override
	public DataHolder<Course> generate() {
		
		var location = locationGenerator.generate();
		var user = userGenerator.generate();
				
		return DataHolder.of(
				
				new Course("232", location.first(), user.first()),
				new Course("326", location.second(), user.second()),
				new Course("121", location.third(), user.third())
			);
	}
}
