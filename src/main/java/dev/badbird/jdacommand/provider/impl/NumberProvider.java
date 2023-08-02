package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.annotation.DefaultNumber;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import dev.badbird.jdacommand.util.Primitives;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class NumberProvider {
    public static class IntProvider implements Provider<Integer> {

        @Override
        public Integer provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option == null) {
                if (parameterContext.hasAnnotation(DefaultNumber.class)) {
                    return (int) parameterContext.getAnnotation(DefaultNumber.class).value();
                } else {
                    return (Integer) Primitives.getDefaultValue(int.class);
                }
            }
            return option.getAsInt();
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.INTEGER;
        }
    }
    public static class FloatProvider implements Provider<Float> {

        @Override
        public Float provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option == null) {
                if (parameterContext.hasAnnotation(DefaultNumber.class)) {
                    return (float) parameterContext.getAnnotation(DefaultNumber.class).value();
                } else {
                    return (Float) Primitives.getDefaultValue(float.class);
                }
            }
            return (float) option.getAsDouble();
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.NUMBER;
        }

        @Override
        public void modifyOptionData(OptionData data) {
            data.setRequiredRange(Float.MIN_VALUE, Float.MAX_VALUE);
        }
    }
    public static class DoubleProvider implements Provider<Double> {

        @Override
        public Double provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option == null) {
                if (parameterContext.hasAnnotation(DefaultNumber.class)) {
                    return parameterContext.getAnnotation(DefaultNumber.class).value();
                } else {
                    return (Double) Primitives.getDefaultValue(double.class);
                }
            }
            return option.getAsDouble();
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.NUMBER;
        }
    }
    public static class LongProvider implements Provider<Long> {

        @Override
        public Long provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option == null) {
                if (parameterContext.hasAnnotation(DefaultNumber.class)) {
                    return (long) parameterContext.getAnnotation(DefaultNumber.class).value();
                } else {
                    return (Long) Primitives.getDefaultValue(long.class);
                }
            }
            return option.getAsLong();
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.INTEGER;
        }
    }
    public static class ShortProvider implements Provider<Short> {

        @Override
        public Short provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option == null) {
                if (parameterContext.hasAnnotation(DefaultNumber.class)) {
                    return (short) parameterContext.getAnnotation(DefaultNumber.class).value();
                } else {
                    return (Short) Primitives.getDefaultValue(short.class);
                }
            }
            return (short) option.getAsLong();
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.INTEGER;
        }

        @Override
        public void modifyOptionData(OptionData data) {
            data.setRequiredRange(Short.MIN_VALUE, Short.MAX_VALUE);
        }
    }
    public static class ByteProvider implements Provider<Byte> {

        @Override
        public Byte provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option == null) {
                if (parameterContext.hasAnnotation(DefaultNumber.class)) {
                    return (byte) parameterContext.getAnnotation(DefaultNumber.class).value();
                } else {
                    return (Byte) Primitives.getDefaultValue(byte.class);
                }
            }
            return (byte) option.getAsLong();
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.INTEGER;
        }

        @Override
        public void modifyOptionData(OptionData data) {
            data.setRequiredRange(Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
    }

    public static void registerAll(JDACommand jdaCommand) {
        jdaCommand.registerProvider(Integer.class, new IntProvider());
        jdaCommand.registerProvider(Float.class, new FloatProvider());
        jdaCommand.registerProvider(Double.class, new DoubleProvider());
        jdaCommand.registerProvider(Long.class, new LongProvider());
        jdaCommand.registerProvider(Short.class, new ShortProvider());
        jdaCommand.registerProvider(Byte.class, new ByteProvider());
    }
}
