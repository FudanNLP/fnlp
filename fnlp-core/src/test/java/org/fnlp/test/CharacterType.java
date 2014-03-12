package org.fnlp.test;

public class CharacterType {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Character.getType('我'));
		System.out.println(Character.getType('I'));
		System.out.println(Character.getType(' '));
		System.out.println(Character.getType('。'));
		System.out.println(Character.getType('.'));
		System.out.println(Character.getType('6'));
		System.out.println(Character.getType('⑴'));
		System.out.println(Character.getType('十'));

	}

}
