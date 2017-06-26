package cn.charlie166.study.test.huang;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

class Person implements Comparable<Person>{
	 String name;
	 int age;
	public Person(String name, int age) {
	
		this.name = name;
		this.age = age;
	}
	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + "]";
	}
	public int compareTo(Person other) {
		if(age > other.age){
			return 1;
		}else if(age < other.age){
			return -1;
		}
		return 0;
	}
	
}
public class TreeSet6Demo {
	public static void main(String[] args) {
		Set<Person> set = new TreeSet<>(new NameLength());
		set.add(new Person("西门吹雪" , 75));
		set.add(new Person("记" , 32));
		set.add(new Person("陆小凤" , 59));
		set.add(new Person("大大" , 7));
		System.out.println(set);
		Set<Person> set2 = new TreeSet<>();
		set2.add(new Person("西门吹雪" , 75));
		set2.add(new Person("赵" , 32));
		set2.add(new Person("陆小凤" , 59));
		set2.add(new Person("西门吹猪" , 17));
		System.out.println(set2);
	}
}
//定制比较
class NameLength implements Comparator<Person>{

	@Override
	public int compare(Person o1, Person o2) {
		if(o1.name.length() > o2.name.length()){
			return 1;
		}else if(o1.name.length() < o2.name.length()){
			return -1;
		}
		return 0;
	}
	
}