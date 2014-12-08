## Usage


```java
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.valid.ValidationError;
import com.shapesecurity.laserbat.js.valid.Validator;

String js = "whatever";
Program p = Parser.parse(js);
List<ValidationError> errors = Validator.validate(p);
Boolean isValid = errors.isEmpty();
```
