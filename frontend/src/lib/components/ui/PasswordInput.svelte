<script lang="ts">
  export let field: string = "";
  export let label: string = "Password";
  export let placeholder: string = "Password";
  export let value: string = "";

  let ispasswordHidden: boolean = true;
  let passwordType: string;
  $: {
    passwordType = ispasswordHidden ? "password" : "text";
  }
</script>

<div>
  <label for="password" class="label">
    <span class="label-text font-bold">{label}</span>
  </label>
  <!-- it seems like the focus properties of input input-bordered do not translate to a div... therefore I tried to imitate the behaviour-->
  <div
    class="flex input input-bordered w-full bg-gray-800 text-white
    focus-within:ring-2 focus-within:ring-offset-2 first-line:focus-within:ring-opacity-50
    focus-within:ring-gray-300 dark:focus-within:ring-gray-700
    focus-within:ring-offset-theme-offset-color dark:focus-within:ring-offset-gray-900 "
  >
    <div class="flex-grow flex">
      <input
        id="password"
        {value}
        name={field}
        type={passwordType}
        {placeholder}
        class="w-full appearance-none bg-gray-800 text-white focus:outline-none"
      />
    </div>
    <div class="flex-shrink">
      <input
        type="checkbox"
        id="eye"
        bind:checked={ispasswordHidden}
        class="hidden"
      />
      <label for="eye" class="label text-gray-400">
        {#if ispasswordHidden}
          <span class="hover:underline">show</span>
        {:else}
          <span class="hover:underline"> hide </span>
        {/if}
      </label>
    </div>
  </div>
</div>
