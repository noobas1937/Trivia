/** Created by Jack Chen at 12/27/2014 */
package cucumber.runtime;

import com.ecnu.trivia.common.component.test.testng.SpringStartupListener;
import org.springframework.test.context.ContextConfiguration;

/** @author Jack Chen */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class ApplicationContextListener extends SpringStartupListener {
}
