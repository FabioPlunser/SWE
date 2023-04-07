<script lang="ts">
  import Input from "$components/ui/Input.svelte";
  import CheckRing from "$lib/assets/icons/checkRing.svg?component";
  export let data;
  let roles: string[] = Array.from(data.userPermissions.keys());
</script>

<h1>Profile</h1>

<section class="w-full">
  <form action="" method="get" class="flex justify-center">
    <div class="w-full max-w-md">
      <Input
        type="text"
        field="username"
        label="Username"
        placeholder="Username"
        value={data.username}
      />
      <Input
        type="email"
        field="email"
        label="Email"
        placeholder="example.mail@planthealth.com"
        value={data.userEmail}
      />
      <Input
        type="password"
        field="password"
        label="Password"
        placeholder="Password"
        value={data.userPassword}
      />
      <!--
      <button class="btn btn-primary mt-4 flex justify-center mx-auto"
        >Update</button
      >
    -->
      <div class="mt-6">
        <ul class="grid w-full gap-6 md:grid-rows-3">
          {#each roles as role}
            <li class="float-root peer-checked:border-green">
              {data.userPermissions.get(role)}
              <input
                type="checkbox"
                id={role}
                value={data.userPermissions.get(role)}
                class="hidden peer"
              />
              <label
                for={role}
                class="btn-primary inline-flex w-full p-3 border-2 rounded-2xl peer-checked:border-gray-700 dark:peer-checked:border-white cursor-pointer justify-between"
              >
                <div>
                  <h1 class="text-lg font-semibold capitalize">{role}</h1>
                </div>
                <div>
                  {#if data.userPermissions.get(role)}
                    <CheckRing class="fill-none" />
                  {/if}
                </div>
              </label>
            </li>
          {/each}
        </ul>
      </div>
    </div>
  </form>
</section>
