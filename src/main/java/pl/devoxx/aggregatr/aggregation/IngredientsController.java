package pl.devoxx.aggregatr.aggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.devoxx.aggregatr.aggregation.model.Ingredients;
import pl.devoxx.aggregatr.aggregation.model.Order;
import pl.devoxx.aggregatr.aggregation.model.Version;

import java.util.Random;

@RestController
@RequestMapping(value = "/ingredients", consumes = Version.AGGREGATOR_V1, produces = MediaType.APPLICATION_JSON_VALUE)
public class IngredientsController {

    private final IngredientsAggregator ingredientsAggregator;
    private final Trace trace;

    @Autowired
    public IngredientsController(IngredientsAggregator ingredientsAggregator, Trace trace) {
        this.ingredientsAggregator = ingredientsAggregator;
        this.trace = trace;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Ingredients distributeIngredients(@RequestBody Order order) {
        final Random random = new Random();
        int millis = random.nextInt(1000);
        Ingredients ingredients = ingredientsAggregator.fetchIngredients(order);
        this.trace.addAnnotation("random-sleep-millis", String.valueOf(millis));
        return ingredients;
    }

}
