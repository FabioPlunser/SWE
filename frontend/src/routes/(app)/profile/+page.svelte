<script lang="ts">
  import Input from "$components/ui/Input.svelte";
</script>

<h1>Profile</h1>

<section class="w-full">
  <div class="flex justify-center items-center w-full ">
    <div class="w-full max-w-md">
      <Input field="username" label="Username" />
      <Input field="email" label="Email" />
      <button class="btn btn-primary mt-4 flex justify-center mx-auto"
        >Update</button
      >
    </div>
  </div>
</section>
