package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.MaxLength;
import dev.badbird.jdacommand.annotation.MinLength;
import dev.badbird.jdacommand.annotation.Range;
import dev.badbird.jdacommand.object.resolver.ParameterResolver;
import dev.badbird.jdacommand.object.resolver.impl.DefaultParameterResolver;
import dev.badbird.jdacommand.provider.Provider;
import dev.badbird.jdacommand.util.Primitives;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.Parameter;
import java.util.Arrays;

@Getter
@Setter
public class ParameterInfo {
    private final Parameter parameter;
    private final JDACommand jdaCommand;
    private Provider<?> provider;
    private boolean alreadyFoundProvider = false;

    public ParameterInfo(Parameter parameter, JDACommand jdaCommand) {
        this.parameter = parameter;
        this.jdaCommand = jdaCommand;
    }

    public boolean isArgument() {
        return parameter.isAnnotationPresent(Arg.class);
    }

    public Arg getArgumentData() {
        return parameter.getDeclaredAnnotation(Arg.class);
    }

    public OptionType resolveType() {
        Provider<?> provider = getProvider();
        System.out.println("Provider is " + provider);
        if (provider != null)
            return provider.getOptionType(this);
        return null;
    }

    public OptionData generateOption() {
        Arg arg = getArgumentData();
        OptionType type = resolveType();
        if (type == null)
            throw new IllegalArgumentException("Could not resolve type for parameter " + arg.value());
        OptionData data = new OptionData(type, arg.value(), arg.description(), arg.required());
        getProvider().modifyOptionData(data);
        data.setRequired(arg.required());
        if (parameter.isAnnotationPresent(Range.class)) {
            Range range = parameter.getDeclaredAnnotation(Range.class);
            long minLong = range.minLong();
            long maxLong = range.maxLong();
            if (minLong != Long.MIN_VALUE) {
                data.setMinValue(minLong);
            } else if (maxLong != Long.MAX_VALUE) {
                data.setMaxValue(maxLong);
            }
            double minDouble = range.minDouble();
            double maxDouble = range.maxDouble();
            if (minDouble != Double.MIN_VALUE) {
                data.setMinValue(minDouble);
            } else if (maxDouble != Double.MAX_VALUE) {
                data.setMaxValue(maxDouble);
            }
        }
        if (parameter.isAnnotationPresent(MaxLength.class)) {
            data.setMaxLength(parameter.getDeclaredAnnotation(MaxLength.class).value());
        }
        if (parameter.isAnnotationPresent(MinLength.class)) {
            data.setMinLength(parameter.getDeclaredAnnotation(MinLength.class).value());
        }
        // TODO: auto complete api
        // TODO: callback api to modify OptionData
        return data;
    }

    public Provider<?> getProvider() {
        if (provider == null && !alreadyFoundProvider) {
            jdaCommand.getArgumentProviders().entrySet().stream().filter(entry -> {
                Class<?> clazz = entry.getKey();
                Provider<?> provider = entry.getValue();
                boolean matchWithInstanceOf = provider.matchWithInstanceOf();
                if (matchWithInstanceOf)
                    return Primitives.wrap(parameter.getType()).isAssignableFrom(clazz) || (provider.getExtraTypes() != null && Arrays.stream(provider.getExtraTypes()).anyMatch(t -> Primitives.wrap(parameter.getType()).isAssignableFrom(t)));
                else
                    return clazz.equals(Primitives.wrap(parameter.getType())) || (provider.getExtraTypes() != null && Arrays.asList(provider.getExtraTypes()).contains(Primitives.wrap(parameter.getType())));
            }).findFirst().ifPresent(classProviderEntry -> provider = classProviderEntry.getValue());
            alreadyFoundProvider = true;
        }
        return provider;
    }

    public ParameterResolver getResolver() {
        return DefaultParameterResolver.INSTANCE;
    }
}
