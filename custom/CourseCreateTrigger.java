trigger CourseTrainUpdateFields on ModuleCourseTranning__c (after insert, after update, after delete) {
	List<ModuleCourseTranning__c> modules_course = (!Trigger.isDelete) ?Trigger.new : Trigger.old;
    Map<Id, CourseTraining__c> courses = new Map<Id, CourseTraining__c>();
    
    String link_video = '';
    String link_test = '';
    String require_listener = '';
    String programm_course = '';
    Decimal hours = 0;
    List<Attachment> course_attach=new List<Attachment>();
    
    for (ModuleCourseTranning__c module_course : modules_course) {
        list<ModuleCourseTranning__c> module_exists = [select ModuleCourse__c from ModuleCourseTranning__c where ModuleCourse__c = :module_course.ModuleCourse__c and CourseModule__c = :module_course.CourseModule__c];
        if (module_exists.size() > 1 && !Trigger.isDelete) {
            module_course.ModuleCourse__c.addError('Такой модуль уже существует у этого курса');
            return;
        }
    	
        CourseTraining__c course = [select Id, LinkToVideo__c, LinkToTest__c, NumberOfHours__c, RequireListener__c, ProgrammCourse__c from CourseTraining__c where Id = :module_course.CourseModule__c];
        list<ModuleTraining__c> modules_search = [select Id, LinkToVideo__c, LinkToTest__c, NumberOfHours__c, Body__c, InRequireListener__c from ModuleTraining__c 
                                           where Id in ( select ModuleCourse__c from ModuleCourseTranning__c where CourseModule__c = :course.Id ) order by Name ];
        for (ModuleTraining__c module_search : modules_search) {
            if(Trigger.isDelete && module_search.Id == module_course.ModuleCourse__c) {
            	continue;		
            } 
            link_video += module_search.LinkToVideo__c+'\n';
            link_test += module_search.LinkToTest__c+'\n';
            require_listener += module_search.InRequireListener__c+'\n';
            programm_course += module_search.Body__c+'\n';
            hours += module_search.NumberOfHours__c;   
            
            for(Attachment a:[Select Id,ParentId,OwnerId,CreatedById,Name,ContentType,Body,BodyLength,Description from Attachment where ParentId=:module_search.Id]){  
              Attachment newFile = a.clone();
              newFile.ParentId = course.Id;
              course_attach.add(newFile);
   			}
        }
              
        course.LinkToVideo__c = link_video;
        course.LinkToTest__c = link_test;
        course.RequireListener__c = require_listener;
        course.ProgrammCourse__c = programm_course;
        course.NumberOfHours__c = hours;
        
        courses.put(course.Id, course);
                
    }
   
    
    if (courses.isEmpty()) {
        return;
    }
    
    update courses.values();
    
     if (course_attach.isEmpty()) {
        return;
     } else {
        list<Attachment> attach_del = [select Id from Attachment where ParentId in :courses.keySet()];
         delete attach_del;
     }
    
    insert course_attach;    
    
}