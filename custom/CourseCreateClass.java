@istest
public class CourseTrainUpdateFields_TEST {
    static public testmethod void Test() { 
        ModuleTraining__c module_new = new ModuleTraining__c(Name='Test module', LinkToVideo__c='http://ya.ru', Body__c='Test, Test test', NumberOfHours__c=12);
        insert module_new;
        
        CourseTraining__c course_new = new CourseTraining__c(Name='Test course', Abbreviation__c='Test', Qualification__c='Test');
        insert course_new;
        
        
        ModuleCourseTranning__c module_course_new = new ModuleCourseTranning__c(CourseModule__c=course_new.Id, ModuleCourse__c=module_new.Id);
        insert module_course_new;
        
        CourseTraining__c course_one =  [Select Id, LinkToVideo__c, NumberOfHours__c from CourseTraining__c where Id = :course_new.Id];
         
        system.assertEquals(course_one.NumberOfHours__c, 12);
        
        
        ModuleTraining__c module_new2 = new ModuleTraining__c(Name='Test2 module', LinkToVideo__c='http://google.ru', Body__c='Test2, Test2 test2', NumberOfHours__c=20);
        insert module_new2;
        
        ModuleCourseTranning__c module_course_new2 = new ModuleCourseTranning__c(CourseModule__c=course_new.Id, ModuleCourse__c=module_new2.Id);
        insert module_course_new2;
        CourseTraining__c course_one2 =  [Select Id, LinkToVideo__c, NumberOfHours__c from CourseTraining__c where Id = :course_new.Id];
        system.assertEquals(course_one2.NumberOfHours__c, 32);
        
        ModuleCourseTranning__c module_course_new3 = new ModuleCourseTranning__c(Id=module_course_new2.Id);
        delete module_course_new3;
        
        CourseTraining__c course_one3 =  [Select Id, LinkToVideo__c, NumberOfHours__c from CourseTraining__c where Id = :course_new.Id];
        system.assertEquals(course_one3.NumberOfHours__c, 12);
        
    }
}