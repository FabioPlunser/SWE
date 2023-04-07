<script lang="ts">
  import Input from "$components/ui/Input.svelte";
  import CheckRing from "$lib/assets/icons/checkRing.svg?component";
  import BooleanListItem from "$lib/components/ui/BooleanListItem.svelte";

  export let data;

  let roles: string[] = Array.from(Object.keys(data.userPermissions));
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
            <BooleanListItem text={role} bool={data.userPermissions[role]} />
          {/each}
        </ul>
      </div>
    </div>
  </form>
</section>
