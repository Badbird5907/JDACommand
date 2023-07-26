package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.provider.Provider;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

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
            return provider.getOptionType();
        return null;
    }

    public Provider<?> getProvider() {
        if (provider == null && !alreadyFoundProvider) {
            jdaCommand.getArgumentProviders().entrySet().stream().filter(entry -> {
                Class<?> clazz = entry.getKey();
                Provider<?> provider = entry.getValue();
                boolean matchWithInstanceOf = provider.matchWithInstanceOf();
                if (matchWithInstanceOf)
                    return parameter.getType().isAssignableFrom(clazz) || (provider.getExtraTypes() != null && Arrays.stream(provider.getExtraTypes()).anyMatch(t -> parameter.getType().isAssignableFrom(t)));
                else
                    return clazz.equals(parameter.getType()) || (provider.getExtraTypes() != null && Arrays.asList(provider.getExtraTypes()).contains(parameter.getType()));
            }).findFirst().ifPresent(classProviderEntry -> provider = classProviderEntry.getValue());
            alreadyFoundProvider = true;
        }
        return provider;
    }
}
