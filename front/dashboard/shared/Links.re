open Types;

let dashboardHomeUrl = "/";
let coursesUrl = dashboardHomeUrl;

let coursePageUrl = (course, pageId) =>
  "/courses/" ++ course.id ++ "/" ++ pageId;
