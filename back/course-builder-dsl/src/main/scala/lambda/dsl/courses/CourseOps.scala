package lambda.dsl.courses

import lambda.domain.Image
import lambda.domain.courses.{Course, Page}

trait CourseOps {
  implicit class CourseOps(course: Course) {
    def withTitle(title: String): Course = course.copy(title = title)
    def withDescription(description: String): Course = course.copy(description = description)
    def withTags(tags: String*): Course = course.copy(tags = (course.tags ++ tags).distinct)
    def withTag(tag: String): Course = withTags(tag)
    def withImage(image: Image): Course = course.copy(image = Some(image))
    def withPages(pages: Page*): Course = course.copy(pages = course.pages ++ pages)
    def withPage(page: Page): Course = withPages(page)
  }
}
