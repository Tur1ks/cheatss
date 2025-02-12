package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeCommand {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.recipe.give.failed"));
    private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.recipe.take.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(
            Commands.literal("recipe")
                .requires(p_138205_ -> p_138205_.hasPermission(2))
                .then(
                    Commands.literal("give")
                        .then(
                            Commands.argument("targets", EntityArgument.players())
                                .then(
                                    Commands.argument("recipe", ResourceLocationArgument.id())
                                        .suggests(SuggestionProviders.ALL_RECIPES)
                                        .executes(
                                            p_296531_ -> giveRecipes(
                                                    p_296531_.getSource(),
                                                    EntityArgument.getPlayers(p_296531_, "targets"),
                                                    Collections.singleton(ResourceLocationArgument.getRecipe(p_296531_, "recipe"))
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("*")
                                        .executes(
                                            p_138217_ -> giveRecipes(
                                                    p_138217_.getSource(),
                                                    EntityArgument.getPlayers(p_138217_, "targets"),
                                                    p_138217_.getSource().getServer().getRecipeManager().getRecipes()
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("take")
                        .then(
                            Commands.argument("targets", EntityArgument.players())
                                .then(
                                    Commands.argument("recipe", ResourceLocationArgument.id())
                                        .suggests(SuggestionProviders.ALL_RECIPES)
                                        .executes(
                                            p_296532_ -> takeRecipes(
                                                    p_296532_.getSource(),
                                                    EntityArgument.getPlayers(p_296532_, "targets"),
                                                    Collections.singleton(ResourceLocationArgument.getRecipe(p_296532_, "recipe"))
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("*")
                                        .executes(
                                            p_138203_ -> takeRecipes(
                                                    p_138203_.getSource(),
                                                    EntityArgument.getPlayers(p_138203_, "targets"),
                                                    p_138203_.getSource().getServer().getRecipeManager().getRecipes()
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int giveRecipes(CommandSourceStack pSource, Collection<ServerPlayer> pTargets, Collection<RecipeHolder<?>> pRecipes) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer serverplayer : pTargets) {
            i += serverplayer.awardRecipes(pRecipes);
        }

        if (i == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(
                    () -> Component.translatable("commands.recipe.give.success.single", pRecipes.size(), pTargets.iterator().next().getDisplayName()), true
                );
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.recipe.give.success.multiple", pRecipes.size(), pTargets.size()), true);
            }

            return i;
        }
    }

    private static int takeRecipes(CommandSourceStack pSource, Collection<ServerPlayer> pTargets, Collection<RecipeHolder<?>> pRecipes) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer serverplayer : pTargets) {
            i += serverplayer.resetRecipes(pRecipes);
        }

        if (i == 0) {
            throw ERROR_TAKE_FAILED.create();
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(
                    () -> Component.translatable("commands.recipe.take.success.single", pRecipes.size(), pTargets.iterator().next().getDisplayName()), true
                );
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.recipe.take.success.multiple", pRecipes.size(), pTargets.size()), true);
            }

            return i;
        }
    }
}