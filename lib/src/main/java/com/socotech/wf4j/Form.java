package com.socotech.wf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;

/**
 * Form.java
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Form {
	/**
	 * The canonical name of this form
	 *
	 * @return form name
	 */
	String name() default "form";

	/**
	 * The class used to represent form in an action
	 *
	 * @return form class
	 */
	@SuppressWarnings("unchecked")
	Class formClass();

	/**
	 * An optional class used to validate the contents of the form
	 *
	 * @return validator class
	 */
	@SuppressWarnings("unchecked")
	Class validatorClass() default void.class;

	/**
	 * Binders to use on form properties
	 *
	 * @return array of binders
	 */
	FormBinder[] binders() default {};

	/**
	 * If true, persist the form in the user's session between form view and form submission
	 *
	 * @return true if session form
	 */
	boolean sessionForm() default false;
}
