package test;

import br.com.gamemods.qep.QuickExpressionParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UsageTest {
	@Test
	public void test() {
		Map<String, Object> user = new HashMap<>();
		user.put("name", "Michael");
		user.put("isAdult", true);
		
		Map<String, Object> context = new HashMap<>();
		context.put("dayOfWeek", "monday");
		context.put("user", user);
		
		String result = QuickExpressionParser.parseExpression(
				"Hello #{user.name}, today is #dayOfWeek. Will you go #{user.isAdult? 'work' : 'to school'} today?",
				context
		);
		
		Assert.assertEquals(
				"Hello Michael, today is monday. Will you go work today?",
				result
		);
	}
}
