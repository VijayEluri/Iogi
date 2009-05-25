package iogi.conversion;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import org.junit.Test;

public class TypeConverterTests {
	@Test
	public void callingInstantiateWillTriggerACallToTheConvertAbstractMethod() throws Exception {
		final Object stubConverted = new Object();
		class MockTypeConverter extends TypeConverter<Object> {
			@Override
			protected Object convert(String stringValue, Target<?> to) {
				return stubConverted;
			}
			
			@Override
			public boolean isAbleToInstantiate(Target<?> target) {
				return false;
			}
		}

		MockTypeConverter typeConverter = new MockTypeConverter();
		Object instantiated = typeConverter.instantiate(Target.create(String.class, ""), new Parameters(new Parameter("", "")));
		assertEquals(stubConverted, instantiated);
	}
	
	@Test
	public void ifAnArbitratyExceptionIsThrownWhileConvertingItWillBeWrappedInAConvertionException() throws Exception {
		final ArithmeticException expectedWrappedException = new ArithmeticException();
		class MockTypeConverter extends TypeConverter<Fizzble> {
			@Override
			protected Fizzble convert(String stringValue, Target<?> to) {
				throw expectedWrappedException;
			}
			
			@Override
			public boolean isAbleToInstantiate(Target<?> target) {
				return false;
			}
		}
		MockTypeConverter typeConverter = new MockTypeConverter();
		try {
			typeConverter.instantiate(Target.create(Fizzble.class, "foo"), new Parameters(new Parameter("foo", "oops")));
			fail();
		}
		catch(ConversionException thrownException) {
			assertEquals(expectedWrappedException, thrownException.getCause());
			assertThat(thrownException.getMessage(),  both(containsString("Fizzble")).and(containsString("foo")).and(containsString("oops")));
		}
	}
	
	@Test
	public void ifAConvertionExceptionIsThrownWhileConvertingItWillBeRethrown() throws Exception {
		final ConversionException expectedWrappedException = new ConversionException("");
		class MockTypeConverter extends TypeConverter<Fizzble> {
			@Override
			protected Fizzble convert(String stringValue, Target<?> to) {
				throw expectedWrappedException;
			}
			
			@Override
			public boolean isAbleToInstantiate(Target<?> target) {
				return false;
			}
		}
		MockTypeConverter typeConverter = new MockTypeConverter();
		try {
			typeConverter.instantiate(Target.create(Fizzble.class, "foo"), new Parameters(new Parameter("foo", "oops")));
			fail();
		}
		catch(Exception thrownException) {
			assertEquals(expectedWrappedException, thrownException);
		}
	}
	
	
	
	static class Fizzble {}
}