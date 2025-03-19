/**
 * 
 */
package com.privasia.procurehere.core.utils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;

/**
 * @author arc
 */
public class HTMLCharacterEscapes extends CharacterEscapes {

	private static final long serialVersionUID = 8629033364666394568L;

	private final int[] asciiEscapes;

	public HTMLCharacterEscapes() {
		// start with set of characters known to require escaping (double-quote, backslash etc)
		int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
		// and force escaping of a few others:
		esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
		esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
		esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
		esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
//		esc[','] = CharacterEscapes.ESCAPE_STANDARD;
		asciiEscapes = esc;
	}

	// this method gets called for character codes 0 - 127
	@Override
	public int[] getEscapeCodesForAscii() {
		return asciiEscapes;
	}

	// and this for others; we don't need anything special here
	@Override
	public SerializableString getEscapeSequence(int ch) {
		// no further escaping (beyond ASCII chars) needed:
		return null;
	}

}
